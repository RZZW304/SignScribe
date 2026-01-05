package com.signscribe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class SignScribeStatistics {
	private static SignScribeStatistics INSTANCE;

	public static SignScribeStatistics getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignScribeStatistics();
		}
		return INSTANCE;
	}

	public GeneralStats getGeneralStats(Path txthDir) {
		GeneralStats stats = new GeneralStats();

		File[] files = txthDir.toFile().listFiles((dir, name) -> name.endsWith(".txth"));
		if (files != null) {
			stats.totalFiles = files.length;
			for (File file : files) {
				stats.totalSize += file.length();
			}
		}

		stats.totalTemplates = SignTemplateManager.getInstance().getTemplateCount();
		stats.totalSessionTemplates = SessionTemplateManager.getInstance().getTemplateCount();
		stats.totalHistoryRecords = SessionHistoryManager.getInstance().getHistorySize();
		stats.activeSession = SignScribePlacement.getInstance().hasSession();

		return stats;
	}

	public FileStats getFileStats(List<String> filenames, Path txthDir) {
		FileStats stats = new FileStats();

		for (String filename : filenames) {
			File file = txthDir.resolve(filename).toFile();
			if (file.exists()) {
				stats.totalFiles++;
				stats.totalSize += file.length();

				try {
					TxthFileParser parser = new TxthFileParser(file.toPath());
					List<SignPage> pages = parser.parse();
					stats.validFiles++;
					stats.totalSigns += pages.size();
					stats.totalCharacters += countCharacters(filename, txthDir);
				} catch (Exception e) {
					stats.invalidFiles++;
				}
			}
		}

		return stats;
	}

	public UsageStats getUsageStats() {
		UsageStats stats = new UsageStats();

		Map<String, SessionHistoryManager.SessionStats> fileStats =
			SessionHistoryManager.getInstance().getFileStatistics();

		for (Map.Entry<String, SessionHistoryManager.SessionStats> entry : fileStats.entrySet()) {
			stats.totalSessions += entry.getValue().totalSessions;
			stats.totalSignsPlaced += entry.getValue().totalSignsPlaced;
			stats.totalDuration += entry.getValue().totalDuration;
		}

		List<SignTemplate> templates = SignTemplateManager.getInstance().getTemplates();
		for (SignTemplate template : templates) {
			stats.templateUsage += template.getUsageCount();
		}

		List<SessionTemplate> sessionTemplates = SessionTemplateManager.getInstance().getTemplates();
		for (SessionTemplate template : sessionTemplates) {
			stats.sessionTemplateUsage += template.getUsageCount();
		}

		return stats;
	}

	public TemplateStats getTemplateStats() {
		TemplateStats stats = new TemplateStats();

		List<SignTemplate> signTemplates = SignTemplateManager.getInstance().getTemplates();
		stats.totalSignTemplates = signTemplates.size();
		stats.mostUsedSignTemplates = SignTemplateManager.getInstance().getMostUsedTemplates(5);

		List<SessionTemplate> sessionTemplates = SessionTemplateManager.getInstance().getTemplates();
		stats.totalSessionTemplates = sessionTemplates.size();
		stats.mostUsedSessionTemplates = SessionTemplateManager.getInstance().getMostUsedTemplates(5);

		return stats;
	}

	public List<String> getPopularFiles(int limit) {
		Map<String, SessionHistoryManager.SessionStats> fileStats =
			SessionHistoryManager.getInstance().getFileStatistics();

		List<Map.Entry<String, SessionHistoryManager.SessionStats>> sorted =
			new ArrayList<>(fileStats.entrySet());

		sorted.sort((a, b) -> Integer.compare(
			b.getValue().totalSessions,
			a.getValue().totalSessions
		));

		List<String> result = new ArrayList<>();
		for (int i = 0; i < Math.min(limit, sorted.size()); i++) {
			Map.Entry<String, SessionHistoryManager.SessionStats> entry = sorted.get(i);
			SessionHistoryManager.SessionStats stats = entry.getValue();
			result.add(String.format("%s (%d sessions)", entry.getKey(), stats.totalSessions));
		}
		return result;
	}

	public Map<String, String> generateReport(Path txthDir) {
		Map<String, String> report = new LinkedHashMap<>();

		GeneralStats general = getGeneralStats(txthDir);
		report.put("General Overview", formatGeneralStats(general));

		UsageStats usage = getUsageStats();
		report.put("Usage Statistics", formatUsageStats(usage));

		TemplateStats templates = getTemplateStats();
		report.put("Template Statistics", formatTemplateStats(templates));

		report.put("Popular Files", formatPopularFiles(txthDir));

		return report;
	}

	private long countCharacters(String filename, Path txthDir) throws IOException {
		String content = SignScribeFileManager.getInstance().loadTxthFile(filename);
		return content.length();
	}

	private String formatGeneralStats(GeneralStats stats) {
		return String.format(
			"Files: %d | Size: %.2f KB | Templates: %d | Session Templates: %d | History Records: %d | Active Session: %s",
			stats.totalFiles,
			stats.totalSize / 1024.0,
			stats.totalTemplates,
			stats.totalSessionTemplates,
			stats.totalHistoryRecords,
			stats.activeSession ? "Yes" : "No"
		);
	}

	private String formatUsageStats(UsageStats stats) {
		long avgDuration = stats.totalSessions > 0 ? stats.totalDuration / stats.totalSessions : 0;
		long avgDurationSeconds = avgDuration / 1000;
		long minutes = avgDurationSeconds / 60;
		long seconds = avgDurationSeconds % 60;

		return String.format(
			"Sessions: %d | Signs Placed: %d | Total Time: %d min %d sec | Avg/Session: %d min %d sec",
			stats.totalSessions,
			stats.totalSignsPlaced,
			stats.totalDuration / 1000 / 60,
			(stats.totalDuration / 1000) % 60,
			minutes,
			seconds
		);
	}

	private String formatTemplateStats(TemplateStats stats) {
		return String.format(
			"Sign Templates: %d | Session Templates: %d",
			stats.totalSignTemplates,
			stats.totalSessionTemplates
		);
	}

	private String formatPopularFiles(Path txthDir) {
		List<String> popular = getPopularFiles(5);
		return String.join(", ", popular);
	}

	public static class GeneralStats {
		public int totalFiles = 0;
		public long totalSize = 0;
		public int totalTemplates = 0;
		public int totalSessionTemplates = 0;
		public int totalHistoryRecords = 0;
		public boolean activeSession = false;
	}

	public static class FileStats {
		public int totalFiles = 0;
		public int validFiles = 0;
		public int invalidFiles = 0;
		public long totalSize = 0;
		public int totalSigns = 0;
		public long totalCharacters = 0;
	}

	public static class UsageStats {
		public int totalSessions = 0;
		public int totalSignsPlaced = 0;
		public long totalDuration = 0;
		public int templateUsage = 0;
		public int sessionTemplateUsage = 0;
	}

	public static class TemplateStats {
		public int totalSignTemplates = 0;
		public int totalSessionTemplates = 0;
		public List<SignTemplate> mostUsedSignTemplates = new ArrayList<>();
		public List<SessionTemplate> mostUsedSessionTemplates = new ArrayList<>();
	}
}
