package com.signscribe;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class BackupRestoreManager {
	private static BackupRestoreManager INSTANCE;

	public static BackupRestoreManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BackupRestoreManager();
		}
		return INSTANCE;
	}

	public void createBackup(Path backupPath) throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(backupPath.toFile()))) {
			ZipEntry txthEntry = new ZipEntry("txth_files/");
			zos.putNextEntry(txthEntry);
			zos.closeEntry();

			Path txthDir = SignScribeFileManager.getInstance().getTxthDirectory();
			if (Files.exists(txthDir)) {
				File[] txthFiles = txthDir.toFile().listFiles();
				if (txthFiles != null) {
					for (File file : txthFiles) {
						if (file.isFile()) {
							ZipEntry entry = new ZipEntry("txth_files/" + file.getName());
							zos.putNextEntry(entry);
							Files.copy(file.toPath(), zos);
							zos.closeEntry();
						}
					}
				}
			}

			ZipEntry templatesEntry = new ZipEntry("sign_templates/");
			zos.putNextEntry(templatesEntry);
			zos.closeEntry();

			Path templatesDir = Path.of(
				SignScribeFileManager.getInstance().getTxthDirectory().getParent().toString(),
				"templates"
			);
			if (Files.exists(templatesDir)) {
				File[] templateFiles = templatesDir.toFile().listFiles();
				if (templateFiles != null) {
					for (File file : templateFiles) {
						if (file.isFile()) {
							ZipEntry entry = new ZipEntry("sign_templates/" + file.getName());
							zos.putNextEntry(entry);
							Files.copy(file.toPath(), zos);
							zos.closeEntry();
						}
					}
				}
			}

			ZipEntry sessionTemplatesEntry = new ZipEntry("session_templates/");
			zos.putNextEntry(sessionTemplatesEntry);
			zos.closeEntry();

			Path sessionTemplatesDir = Path.of(
				SignScribeFileManager.getInstance().getTxthDirectory().getParent().toString(),
				"session_templates"
			);
			if (Files.exists(sessionTemplatesDir)) {
				File[] sessionTemplateFiles = sessionTemplatesDir.toFile().listFiles();
				if (sessionTemplateFiles != null) {
					for (File file : sessionTemplateFiles) {
						if (file.isFile()) {
							ZipEntry entry = new ZipEntry("session_templates/" + file.getName());
							zos.putNextEntry(entry);
							Files.copy(file.toPath(), zos);
							zos.closeEntry();
						}
					}
				}
			}

			ZipEntry sessionDataEntry = new ZipEntry("session_data.dat");
			zos.putNextEntry(sessionDataEntry);

			SignScribeData data = SignScribeData.getInstance();
			NbtCompound nbt = new NbtCompound();
			nbt.putString("currentTxthFile", data.getCurrentTxthFile());
			nbt.putInt("currentPageIndex", data.getCurrentPageIndex());
			nbt.putInt("totalSigns", data.getTotalSigns());
			nbt.putBoolean("hasActiveSession", data.hasActiveSession());

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			NbtIo.writeCompressed(nbt, baos);
			zos.write(baos.toByteArray());
			zos.closeEntry();

			ZipEntry sessionHistoryEntry = new ZipEntry("session_history.log");
			zos.putNextEntry(sessionHistoryEntry);

			Path historyFile = Path.of(
				SignScribeFileManager.getInstance().getTxthDirectory().getParent().toString(),
				"session_history.log"
			);
			if (Files.exists(historyFile)) {
				Files.copy(historyFile, zos);
			}
			zos.closeEntry();

			ZipEntry metadataEntry = new ZipEntry("backup_metadata.txt");
			zos.putNextEntry(metadataEntry);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String timestamp = sdf.format(new Date());

			SignScribeStatistics.GeneralStats stats = SignScribeStatistics.getInstance().getGeneralStats(
				SignScribeFileManager.getInstance().getTxthDirectory()
			);

			StringBuilder metadata = new StringBuilder();
			metadata.append("SignScribe Backup\n");
			metadata.append("================\n");
			metadata.append("Backup Date: ").append(timestamp).append("\n");
			metadata.append("SignScribe Version: ").append(SignScribeMod.VERSION).append("\n\n");
			metadata.append("Statistics at Backup:\n");
			metadata.append("-------------------\n");
			metadata.append("Files: ").append(stats.totalFiles).append("\n");
			metadata.append("Size: ").append(String.format("%.2f KB", stats.totalSize / 1024.0)).append("\n");
			metadata.append("Templates: ").append(stats.totalTemplates).append("\n");
			metadata.append("Session Templates: ").append(stats.totalSessionTemplates).append("\n");
			metadata.append("History Records: ").append(stats.totalHistoryRecords).append("\n");
			metadata.append("Active Session: ").append(stats.activeSession ? "Yes" : "No").append("\n");

			zos.write(metadata.toString().getBytes());
			zos.closeEntry();
		}
	}

	public void restoreBackup(Path backupPath) throws IOException {
		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(backupPath.toFile()))) {
			ZipEntry entry;

			while ((entry = zis.getNextEntry()) != null) {
				String path = entry.getName();

				if (path.equals("backup_metadata.txt")) {
					zis.closeEntry();
					continue;
				}

				if (path.startsWith("txth_files/")) {
					String filename = path.substring("txth_files/".length());
					Path targetPath = SignScribeFileManager.getInstance().getTxthDirectory().resolve(filename);
					Files.createDirectories(targetPath.getParent());
					Files.copy(new DataInputStream(zis), targetPath);
				} else if (path.startsWith("sign_templates/")) {
					String filename = path.substring("sign_templates/".length());
					Path targetPath = Path.of(
						SignScribeFileManager.getInstance().getTxthDirectory().getParent().toString(),
						"templates",
						filename
					);
					Files.createDirectories(targetPath.getParent());
					Files.copy(new DataInputStream(zis), targetPath);
				} else if (path.startsWith("session_templates/")) {
					String filename = path.substring("session_templates/".length());
					Path targetPath = Path.of(
						SignScribeFileManager.getInstance().getTxthDirectory().getParent().toString(),
						"session_templates",
						filename
					);
					Files.createDirectories(targetPath.getParent());
					Files.copy(new DataInputStream(zis), targetPath);
				} else if (path.equals("session_data.dat")) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					byte[] buffer = new byte[1024];
					int len;
					while ((len = zis.read(buffer)) > 0) {
						baos.write(buffer, 0, len);
					}
					NbtCompound nbt = NbtIo.readCompressed(
						new ByteArrayInputStream(baos.toByteArray()),
						net.minecraft.nbt.NbtSizeTracker.ofUnlimitedBytes()
					);
					SignScribeData.getInstance().setCurrentTxthFile(nbt.getString("currentTxthFile").orElse(""));
					SignScribeData.getInstance().setCurrentPageIndex(nbt.getInt("currentPageIndex").orElse(0));
					SignScribeData.getInstance().setTotalSigns(nbt.getInt("totalSigns").orElse(0));
					SignScribeData.getInstance().setHasActiveSession(nbt.getBoolean("hasActiveSession").orElse(false));
					SignScribeData.getInstance().save();
				} else if (path.equals("session_history.log")) {
					Path targetPath = Path.of(
						SignScribeFileManager.getInstance().getTxthDirectory().getParent().toString(),
						"session_history.log"
					);
					Files.copy(new DataInputStream(zis), targetPath);
				}

				zis.closeEntry();
			}
		}

		SignTemplateManager.getInstance().loadTemplates();
		SessionTemplateManager.getInstance().loadTemplates();
		SessionHistoryManager.getInstance().loadHistory();
	}

	public void createScheduledBackup(Path backupDir, int daysBetweenBackups) throws IOException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		String timestamp = sdf.format(new Date());
		Path backupPath = backupDir.resolve("SignScribe_backup_" + timestamp + ".zip");

		File[] existingBackups = backupDir.toFile().listFiles((dir, name) -> name.startsWith("SignScribe_backup_") && name.endsWith(".zip"));
		if (existingBackups != null) {
			for (File file : existingBackups) {
				if (System.currentTimeMillis() - file.lastModified() > daysBetweenBackups * 24L * 60L * 60L * 1000L) {
					Files.delete(file.toPath());
				}
			}
		}

		createBackup(backupPath);
	}

	public String generateBackupName() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
		return "SignScribe_backup_" + sdf.format(new Date()) + ".zip";
	}
}
