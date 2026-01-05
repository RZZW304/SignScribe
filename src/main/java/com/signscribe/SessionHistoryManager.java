package com.signscribe;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class SessionHistoryManager {
	private static SessionHistoryManager INSTANCE;
	private Path historyFile;
	private List<SessionRecord> history;

	public static SessionHistoryManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SessionHistoryManager();
		}
		return INSTANCE;
	}

	public void setHistoryFile(Path configDir) {
		this.historyFile = configDir.resolve("signscribe").resolve("session_history.log");
		this.history = new ArrayList<>();
		try {
			loadHistory();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void recordSession(String filename, int signCount, long duration) {
		SessionRecord record = new SessionRecord(
			filename,
			signCount,
			duration,
			System.currentTimeMillis()
		);
		history.add(0, record);
		try {
			saveHistory();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void recordSessionStart(String filename) {
		SessionRecord record = new SessionRecord(
			filename,
			0,
			-1,
			System.currentTimeMillis()
		);
		history.add(0, record);
		try {
			saveHistory();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void recordSessionEnd(SessionRecord record, int signsPlaced, long duration) {
		record.signsPlaced = signsPlaced;
		record.duration = duration;
		try {
			saveHistory();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void loadHistory() throws IOException {
		if (!Files.exists(historyFile)) {
			return;
		}

		history = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(historyFile)) {
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}
				SessionRecord record = SessionRecord.fromString(line);
				if (record != null) {
					history.add(record);
				}
			}
		}
	}

	private void saveHistory() throws IOException {
		try (BufferedWriter writer = Files.newBufferedWriter(historyFile)) {
			for (SessionRecord record : history) {
				writer.write(record.toString());
				writer.newLine();
			}
		}
	}

	public List<SessionRecord> getHistory() {
		return new ArrayList<>(history);
	}

	public List<SessionRecord> getHistoryByFilename(String filename) {
		List<SessionRecord> result = new ArrayList<>();
		for (SessionRecord record : history) {
			if (record.filename.equals(filename)) {
				result.add(record);
			}
		}
		return result;
	}

	public List<SessionRecord> getRecentSessions(int limit) {
		List<SessionRecord> result = new ArrayList<>(history);
		result.sort((a, b) -> Long.compare(b.timestamp, a.timestamp));
		return result.subList(0, Math.min(limit, result.size()));
	}

	public List<SessionRecord> getMostPlayedFiles(int limit) {
		Map<String, Integer> fileCounts = new HashMap<>();
		for (SessionRecord record : history) {
			if (record.signsPlaced > 0) {
				fileCounts.put(record.filename, fileCounts.getOrDefault(record.filename, 0) + 1);
			}
		}
		List<Map.Entry<String, Integer>> sorted = new ArrayList<>(fileCounts.entrySet());
		sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

		List<SessionRecord> result = new ArrayList<>();
		for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
			Map.Entry<String, Integer> entry = sorted.get(i);
			SessionRecord latestRecord = null;
			long latestTime = 0;
			for (SessionRecord record : history) {
				if (record.filename.equals(entry.getKey()) && record.timestamp > latestTime) {
					latestTime = record.timestamp;
					latestRecord = record;
				}
			}
			if (latestRecord != null) {
				result.add(latestRecord);
			}
		}
		return result;
	}

	public List<SessionRecord> getLongestSessions(int limit) {
		List<SessionRecord> result = new ArrayList<>(history);
		result.sort((a, b) -> Long.compare(b.duration, a.duration));
		List<SessionRecord> filtered = new ArrayList<>();
		for (SessionRecord record : result) {
			if (record.duration > 0) {
				filtered.add(record);
			}
		}
		return filtered.subList(0, Math.min(limit, filtered.size()));
	}

	public Map<String, SessionStats> getFileStatistics() {
		Map<String, SessionStats> stats = new HashMap<>();
		for (SessionRecord record : history) {
			if (!stats.containsKey(record.filename)) {
				stats.put(record.filename, new SessionStats(record.filename));
			}
			SessionStats fileStats = stats.get(record.filename);
			fileStats.totalSessions++;
			if (record.signsPlaced > 0) {
				fileStats.totalSignsPlaced += record.signsPlaced;
			}
			if (record.duration > 0) {
				fileStats.totalDuration += record.duration;
			}
		}
		return stats;
	}

	public void clearHistory() throws IOException {
		history = new ArrayList<>();
		saveHistory();
	}

	public int getHistorySize() {
		return history.size();
	}

	public static class SessionRecord {
		public String filename;
		public int signsPlaced;
		public long duration;
		public long timestamp;

		public SessionRecord(String filename, int signsPlaced, long duration, long timestamp) {
			this.filename = filename;
			this.signsPlaced = signsPlaced;
			this.duration = duration;
			this.timestamp = timestamp;
		}

		public static SessionRecord fromString(String line) {
			try {
				String[] parts = line.split("\\|");
				if (parts.length != 4) {
					return null;
				}
				String filename = parts[0];
				int signsPlaced = Integer.parseInt(parts[1]);
				long duration = Long.parseLong(parts[2]);
				long timestamp = Long.parseLong(parts[3]);
				return new SessionRecord(filename, signsPlaced, duration, timestamp);
			} catch (Exception e) {
				return null;
			}
		}

		@Override
		public String toString() {
			return filename + "|" + signsPlaced + "|" + duration + "|" + timestamp;
		}

		public String getTimestampFormatted() {
			Date date = new Date(timestamp);
			return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		}

		public String getDurationFormatted() {
			if (duration < 0) {
				return "In progress";
			}
			long seconds = duration / 1000;
			long minutes = seconds / 60;
			long hours = minutes / 60;
			if (hours > 0) {
				return String.format("%dh %dm", hours, minutes % 60);
			} else if (minutes > 0) {
				return String.format("%dm %ds", minutes, seconds % 60);
			} else {
				return seconds + "s";
			}
		}
	}

	public static class SessionStats {
		public String filename;
		public int totalSessions;
		public int totalSignsPlaced;
		public long totalDuration;

		public SessionStats(String filename) {
			this.filename = filename;
			this.totalSessions = 0;
			this.totalSignsPlaced = 0;
			this.totalDuration = 0;
		}

		public String getAverageDuration() {
			if (totalSessions == 0) {
				return "0s";
			}
			long avg = totalDuration / totalSessions;
			return new SessionRecord(filename, 0, avg, 0).getDurationFormatted();
		}

		public String getAverageSignsPerSession() {
			if (totalSessions == 0) {
				return "0";
			}
			return String.format("%.1f", (double)totalSignsPlaced / totalSessions);
		}
	}
}
