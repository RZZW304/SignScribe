package com.signscribe;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ImportExportManager {
	private static ImportExportManager INSTANCE;

	public static ImportExportManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ImportExportManager();
		}
		return INSTANCE;
	}

	public void exportSession(Path exportPath) throws IOException {
		SignScribePlacement placement = SignScribePlacement.getInstance();
		SignScribeData data = SignScribeData.getInstance();
		SignScribeFileManager fileManager = SignScribeFileManager.getInstance();

		if (!placement.hasSession()) {
			throw new IOException("No active session to export");
		}

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(exportPath.toFile()))) {
			ZipEntry sessionEntry = new ZipEntry("session.dat");
			zos.putNextEntry(sessionEntry);
			NbtCompound nbt = new NbtCompound();
			nbt.putString("currentTxthFile", data.getCurrentTxthFile());
			nbt.putInt("currentPageIndex", data.getCurrentPageIndex());
			nbt.putInt("totalSigns", data.getTotalSigns());
			nbt.putBoolean("hasActiveSession", data.hasActiveSession());
			NbtIo.writeCompressed(nbt, zos);
			zos.closeEntry();

			String txthContent = fileManager.loadTxthFile(data.getCurrentTxthFile());
			ZipEntry txthEntry = new ZipEntry(data.getCurrentTxthFile());
			zos.putNextEntry(txthEntry);
			zos.write(txthContent.getBytes());
			zos.closeEntry();
		}
	}

	public void importSession(Path importPath) throws IOException, TxthParseException {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(importPath.toFile()))) {
			ZipEntry entry;
			String txthFilename = null;
			String txthContent = null;
			NbtCompound sessionNbt = null;

			while ((entry = zis.getNextEntry()) != null) {
				if (entry.getName().equals("session.dat")) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len;
					while ((len = zis.read(buffer)) > 0) {
						baos.write(buffer, 0, len);
					}
					sessionNbt = NbtIo.readCompressed(new ByteArrayInputStream(baos.toByteArray()), net.minecraft.nbt.NbtSizeTracker.ofUnlimitedBytes());
				} else if (entry.getName().endsWith(".txth")) {
					txthFilename = entry.getName();
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len;
					while ((len = zis.read(buffer)) > 0) {
						baos.write(buffer, 0, len);
					}
					txthContent = baos.toString();
				}
				zis.closeEntry();
			}

			if (txthFilename == null || txthContent == null) {
				throw new IOException("Invalid session file: missing .txth file");
			}

			if (sessionNbt != null) {
				SignScribeData data = SignScribeData.getInstance();
				data.setCurrentTxthFile(txthFilename);
				data.setCurrentPageIndex(sessionNbt.getInt("currentPageIndex"));
				data.setTotalSigns(sessionNbt.getInt("totalSigns"));
				data.setHasActiveSession(sessionNbt.getBoolean("hasActiveSession"));
				data.save();
			}

			SignScribeFileManager fileManager = SignScribeFileManager.getInstance();
			fileManager.saveTxthFile(txthFilename, txthContent);

			SignScribePlacement placement = SignScribePlacement.getInstance();
			placement.startSession(txthFilename);
		}
	}

	public void exportTemplates(Path exportPath) throws IOException {
		SignTemplateManager templateManager = SignTemplateManager.getInstance();
		List<SignTemplate> templates = templateManager.getTemplates();

		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(exportPath.toFile()))) {
			for (SignTemplate template : templates) {
				String filename = template.getName().replaceAll("[^a-zA-Z0-9-]", "_") + ".template";
				ZipEntry entry = new ZipEntry("templates/" + filename);
				zos.putNextEntry(entry);

				java.util.Properties props = new java.util.Properties();
				props.setProperty("name", template.getName());
				props.setProperty("description", template.getDescription());
				props.setProperty("author", template.getAuthor());
				props.setProperty("line1", template.getLines()[0]);
				props.setProperty("line2", template.getLines()[1]);
				props.setProperty("line3", template.getLines()[2]);
				props.setProperty("line4", template.getLines()[3]);
				props.setProperty("createdTime", String.valueOf(template.getCreatedTime()));
				props.setProperty("lastUsedTime", String.valueOf(template.getLastUsedTime()));
				props.setProperty("usageCount", String.valueOf(template.getUsageCount()));

				props.store(zos, "SignScribe Template: " + template.getName());
				zos.closeEntry();
			}
		}
	}

	public void importTemplates(Path importPath) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(importPath.toFile()))) {
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {
				if (entry.getName().startsWith("templates/") && entry.getName().endsWith(".template")) {
					java.util.Properties props = new java.util.Properties();
					props.load(zis);

					String name = props.getProperty("name");
					String description = props.getProperty("description", "");
					String author = props.getProperty("author", "Unknown");
					String[] lines = {
						props.getProperty("line1", ""),
						props.getProperty("line2", ""),
						props.getProperty("line3", ""),
						props.getProperty("line4", "")
					};

					SignTemplate template = new SignTemplate(name, description, lines, author);

					if (props.containsKey("createdTime")) {
						template.createdTime = Long.parseLong(props.getProperty("createdTime"));
					}
					if (props.containsKey("lastUsedTime")) {
						template.lastUsedTime = Long.parseLong(props.getProperty("lastUsedTime"));
					}
					if (props.containsKey("usageCount")) {
						template.usageCount = Integer.parseInt(props.getProperty("usageCount"));
					}

					SignTemplateManager.getInstance().saveTemplate(template);
				}
				zis.closeEntry();
			}
		}
	}

	public void exportAllData(Path exportPath) throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(exportPath.toFile()))) {
			ZipEntry templatesEntry = new ZipEntry("templates.zip");
			zos.putNextEntry(templatesEntry);

			Path tempTemplates = Files.createTempFile("templates", ".zip");
			exportTemplates(tempTemplates);
			Files.copy(tempTemplates, zos);
			Files.delete(tempTemplates);
			zos.closeEntry();

			SignScribeData data = SignScribeData.getInstance();
			if (data.hasActiveSession()) {
				Path tempSession = Files.createTempFile("session", ".zip");
				exportSession(tempSession);
				ZipEntry sessionEntry = new ZipEntry("session.zip");
				zos.putNextEntry(sessionEntry);
				Files.copy(tempSession, zos);
				Files.delete(tempSession);
				zos.closeEntry();
			}
		}
	}
}
