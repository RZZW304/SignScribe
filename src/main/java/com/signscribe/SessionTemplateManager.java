package com.signscribe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SessionTemplateManager {
	private static SessionTemplateManager INSTANCE;
	private Path templatesDir;
	private List<SessionTemplate> templates;

	public static SessionTemplateManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SessionTemplateManager();
		}
		return INSTANCE;
	}

	public void setTemplatesDirectory(Path configDir) {
		this.templatesDir = configDir.resolve("signscribe").resolve("session_templates");
		this.templates = new ArrayList<>();
		try {
			if (!Files.exists(templatesDir)) {
				Files.createDirectories(templatesDir);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to create session templates directory", e);
		}
	}

	public void loadTemplates() throws IOException {
		this.templates = new ArrayList<>();

		if (!Files.exists(templatesDir)) {
			return;
		}

		File[] templateFiles = templatesDir.toFile().listFiles((dir, name) -> name.endsWith(".zip"));
		if (templateFiles == null) {
			return;
		}

		for (File file : templateFiles) {
			try {
				SessionTemplate template = SessionTemplate.importFrom(file.toPath());
				if (template != null) {
					templates.add(template);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void saveTemplate(SessionTemplate template) throws IOException {
		String filename = template.getName().replaceAll("[^a-zA-Z0-9-]", "_") + ".zip";
		File file = templatesDir.resolve(filename).toFile();

		template.exportTo(file.toPath());

		if (!templates.contains(template)) {
			templates.add(template);
		}
	}

	public void deleteTemplate(SessionTemplate template) throws IOException {
		String filename = template.getName().replaceAll("[^a-zA-Z0-9-]", "_") + ".zip";
		File file = templatesDir.resolve(filename).toFile();

		if (file.exists()) {
			Files.delete(file.toPath());
		}

		templates.remove(template);
	}

	public List<SessionTemplate> getTemplates() {
		return new ArrayList<>(templates);
	}

	public SessionTemplate getTemplateByName(String name) {
		for (SessionTemplate template : templates) {
			if (template.getName().equals(name)) {
				return template;
			}
		}
		return null;
	}

	public SessionTemplate getTemplateById(String templateId) {
		for (SessionTemplate template : templates) {
			if (template.getTemplateId().toString().equals(templateId)) {
				return template;
			}
		}
		return null;
	}

	public List<SessionTemplate> getTemplatesByFilename(String filename) {
		List<SessionTemplate> result = new ArrayList<>();
		for (SessionTemplate template : templates) {
			if (template.getFilename().equals(filename)) {
				result.add(template);
			}
		}
		return result;
	}

	public List<SessionTemplate> getTemplatesByAuthor(String author) {
		List<SessionTemplate> result = new ArrayList<>();
		for (SessionTemplate template : templates) {
			if (template.getAuthor().equals(author)) {
				result.add(template);
			}
		}
		return result;
	}

	public List<SessionTemplate> getMostUsedTemplates(int limit) {
		List<SessionTemplate> sorted = new ArrayList<>(templates);
		sorted.sort((a, b) -> Integer.compare(b.getUsageCount(), a.getUsageCount()));
		return sorted.subList(0, Math.min(limit, sorted.size()));
	}

	public List<SessionTemplate> getRecentlyUsedTemplates(int limit) {
		List<SessionTemplate> sorted = new ArrayList<>(templates);
		sorted.sort((a, b) -> Long.compare(b.getLastUsedTime(), a.getLastUsedTime()));
		return sorted.subList(0, Math.min(limit, sorted.size()));
	}

	public List<SessionTemplate> getTemplatesBySignCount(int minSigns, int maxSigns) {
		List<SessionTemplate> result = new ArrayList<>();
		for (SessionTemplate template : templates) {
			int signCount = template.countSigns();
			if (signCount >= minSigns && signCount <= maxSigns) {
				result.add(template);
			}
		}
		return result;
	}

	public int getTemplateCount() {
		return templates.size();
	}

	public List<String> getAllFilenames() {
		List<String> filenames = new ArrayList<>();
		for (SessionTemplate template : templates) {
			if (!filenames.contains(template.getFilename())) {
				filenames.add(template.getFilename());
			}
		}
		return filenames;
	}
}
