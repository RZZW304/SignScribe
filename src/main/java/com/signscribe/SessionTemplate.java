package com.signscribe;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class SessionTemplate {
	private final String name;
	private final String description;
	private final String filename;
	private final String author;
	private final UUID templateId;
	private final String[] previewLines;
	private long createdTime;
	private long lastUsedTime;
	private int usageCount;

	public SessionTemplate(String name, String description, String filename, String author, String[] previewLines) {
		this.name = name;
		this.description = description != null ? description : "";
		this.filename = filename;
		this.author = author;
		this.templateId = UUID.randomUUID();
		this.previewLines = previewLines != null ? previewLines : new String[]{"", "", "", ""};
		this.createdTime = System.currentTimeMillis();
		this.lastUsedTime = 0;
		this.usageCount = 0;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public String getFilename() {
		return filename;
	}

	public String getAuthor() {
		return author;
	}

	public UUID getTemplateId() {
		return templateId;
	}

	public String[] getPreviewLines() {
		return previewLines;
	}

	public long getCreatedTime() {
		return createdTime;
	}

	public long getLastUsedTime() {
		return lastUsedTime;
	}

	public int getUsageCount() {
		return usageCount;
	}

	public void markAsUsed() {
		this.lastUsedTime = System.currentTimeMillis();
		this.usageCount++;
	}

	public String getSummary() {
		return String.format("%s (%d signs) - %s", name, countSigns(), description);
	}

	public int countSigns() {
		if (previewLines == null) {
			return 0;
		}
		int count = 0;
		for (String line : previewLines) {
			if (line != null && !line.isEmpty()) {
				count++;
			}
		}
		return count;
	}

	public void exportTo(Path exportPath) throws IOException {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(exportPath.toFile()))) {
			ZipEntry metaEntry = new ZipEntry("metadata.properties");
			zos.putNextEntry(metaEntry);
			Properties props = new Properties();
			props.setProperty("name", name);
			props.setProperty("description", description);
			props.setProperty("filename", filename);
			props.setProperty("author", author);
			props.setProperty("templateId", templateId.toString());
			props.setProperty("createdTime", String.valueOf(createdTime));
			props.setProperty("lastUsedTime", String.valueOf(lastUsedTime));
			props.setProperty("usageCount", String.valueOf(usageCount));
			props.store(zos, "SignScribe Session Template");
			zos.closeEntry();

			ZipEntry previewEntry = new ZipEntry("preview.txt");
			zos.putNextEntry(previewEntry);
			for (String line : previewLines) {
				zos.write((line + "\n").getBytes());
			}
			zos.closeEntry();
		}
	}

	public static SessionTemplate importFrom(Path importPath) throws IOException {
		String name = "";
		String description = "";
		String filename = "";
		String author = "Unknown";
		UUID templateId = null;
		long createdTime = 0;
		long lastUsedTime = 0;
		int usageCount = 0;
		String[] previewLines = new String[]{"", "", "", ""};

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(importPath.toFile()))) {
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				if (entry.getName().equals("metadata.properties")) {
					Properties props = new Properties();
					props.load(zis);
					name = props.getProperty("name");
					description = props.getProperty("description", "");
					filename = props.getProperty("filename", "");
					author = props.getProperty("author", "Unknown");
					String templateIdStr = props.getProperty("templateId");
					if (templateIdStr != null) {
						templateId = UUID.fromString(templateIdStr);
					}
					createdTime = Long.parseLong(props.getProperty("createdTime", "0"));
					lastUsedTime = Long.parseLong(props.getProperty("lastUsedTime", "0"));
					usageCount = Integer.parseInt(props.getProperty("usageCount", "0"));
				} else if (entry.getName().equals("preview.txt")) {
					BufferedReader reader = new BufferedReader(new InputStreamReader(zis));
					String line;
					List<String> lines = new ArrayList<>();
					while ((line = reader.readLine()) != null) {
						lines.add(line);
					}
					previewLines = lines.toArray(new String[0]);
				}
				zis.closeEntry();
			}
		}

		SessionTemplate template = new SessionTemplate(name, description, filename, author, previewLines);
		if (templateId != null) {
			template.templateId = templateId;
		}
		template.createdTime = createdTime;
		template.lastUsedTime = lastUsedTime;
		template.usageCount = usageCount;

		return template;
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("description", description);
		map.put("filename", filename);
		map.put("templateId", templateId.toString());
		map.put("author", author);
		map.put("previewLines", previewLines);
		map.put("createdTime", createdTime);
		map.put("lastUsedTime", lastUsedTime);
		map.put("usageCount", usageCount);
		return map;
	}
}
