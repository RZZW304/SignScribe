package com.signscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SignTemplateManager {
	private static SignTemplateManager INSTANCE;
	private Path templatesDir;
	private List<SignTemplate> templates;

	public static SignTemplateManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignTemplateManager();
		}
		return INSTANCE;
	}

	public void setTemplatesDirectory(Path configDir) {
		this.templatesDir = configDir.resolve("signscribe").resolve("templates");
		this.templates = new ArrayList<>();
		try {
			if (!Files.exists(templatesDir)) {
				Files.createDirectories(templatesDir);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to create templates directory", e);
		}
	}

	public void loadTemplates() throws IOException {
		this.templates = new ArrayList<>();

		if (!Files.exists(templatesDir)) {
			return;
		}

		File[] templateFiles = templatesDir.toFile().listFiles((dir, name) -> name.endsWith(".template"));
		if (templateFiles == null) {
			return;
		}

		for (File file : templateFiles) {
			try {
				SignTemplate template = loadTemplate(file);
				if (template != null) {
					templates.add(template);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public SignTemplate loadTemplate(File file) throws IOException {
		Properties props = new Properties();
		try (FileInputStream fis = new FileInputStream(file)) {
			props.load(fis);
		}

		String name = props.getProperty("name", file.getName());
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

		return template;
	}

	public void saveTemplate(SignTemplate template) throws IOException {
		String filename = template.getName().replaceAll("[^a-zA-Z0-9-]", "_") + ".template";
		File file = templatesDir.resolve(filename).toFile();

		Properties props = new Properties();
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

		try (FileOutputStream fos = new FileOutputStream(file)) {
			props.store(fos, "SignScribe Template: " + template.getName());
		}

		if (!templates.contains(template)) {
			templates.add(template);
		}
	}

	public void deleteTemplate(SignTemplate template) throws IOException {
		String filename = template.getName().replaceAll("[^a-zA-Z0-9-]", "_") + ".template";
		File file = templatesDir.resolve(filename).toFile();

		if (file.exists()) {
			Files.delete(file.toPath());
		}

		templates.remove(template);
	}

	public List<SignTemplate> getTemplates() {
		return new ArrayList<>(templates);
	}

	public SignTemplate getTemplateByName(String name) {
		for (SignTemplate template : templates) {
			if (template.getName().equals(name)) {
				return template;
			}
		}
		return null;
	}

	public SignTemplate getTemplateById(String templateId) {
		for (SignTemplate template : templates) {
			if (template.getTemplateId().toString().equals(templateId)) {
				return template;
			}
		}
		return null;
	}

	public List<SignTemplate> getTemplatesByAuthor(String author) {
		List<SignTemplate> result = new ArrayList<>();
		for (SignTemplate template : templates) {
			if (template.getAuthor().equals(author)) {
				result.add(template);
			}
		}
		return result;
	}

	public List<SignTemplate> getMostUsedTemplates(int limit) {
		List<SignTemplate> sorted = new ArrayList<>(templates);
		sorted.sort((a, b) -> Integer.compare(b.getUsageCount(), a.getUsageCount()));
		return sorted.subList(0, Math.min(limit, sorted.size()));
	}

	public List<SignTemplate> getRecentlyUsedTemplates(int limit) {
		List<SignTemplate> sorted = new ArrayList<>(templates);
		sorted.sort((a, b) -> Long.compare(b.getLastUsedTime(), a.getLastUsedTime()));
		return sorted.subList(0, Math.min(limit, sorted.size()));
	}

	public int getTemplateCount() {
		return templates.size();
	}
}
