package com.signscribe;

import com.signscribe.SignPage;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignTemplate {
	private final String name;
	private final String description;
	private final String[] lines;
	private final UUID templateId;
	private final String author;
	private long createdTime;
	private long lastUsedTime;
	private int usageCount;

	public SignTemplate(String name, String description, String[] lines, String author) {
		this.name = name;
		this.description = description != null ? description : "";
		this.lines = lines != null ? lines : new String[]{"", "", "", ""};
		this.templateId = UUID.randomUUID();
		this.author = author;
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

	public String[] getLines() {
		return lines;
	}

	public UUID getTemplateId() {
		return templateId;
	}

	public String getAuthor() {
		return author;
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

	public void setCreatedTime(long createdTime) {
		this.createdTime = createdTime;
	}

	public void setLastUsedTime(long lastUsedTime) {
		this.lastUsedTime = lastUsedTime;
	}

	public void setUsageCount(int usageCount) {
		this.usageCount = usageCount;
	}

	public void markAsUsed() {
		this.lastUsedTime = System.currentTimeMillis();
		this.usageCount++;
	}

	public SignPage toSignPage(int signNumber) {
		return new SignPage(signNumber, lines[0], lines[1], lines[2], lines[3]);
	}

	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("name", name);
		map.put("description", description);
		map.put("templateId", templateId.toString());
		map.put("author", author);
		map.put("lines", lines);
		map.put("createdTime", createdTime);
		map.put("lastUsedTime", lastUsedTime);
		map.put("usageCount", usageCount);
		return map;
	}

	@SuppressWarnings("unchecked")
	public static SignTemplate fromMap(Map<String, Object> map) {
		String name = (String) map.get("name");
		String description = (String) map.getOrDefault("description", "");
		String[] lines = (String[]) map.get("lines");
		String author = (String) map.getOrDefault("author", "Unknown");

		SignTemplate template = new SignTemplate(name, description, lines, author);

		if (map.containsKey("templateId")) {
			template.createdTime = (long) map.getOrDefault("createdTime", System.currentTimeMillis());
			template.lastUsedTime = (long) map.getOrDefault("lastUsedTime", 0L);
			template.usageCount = (int) map.getOrDefault("usageCount", 0);
		}

		return template;
	}
}
