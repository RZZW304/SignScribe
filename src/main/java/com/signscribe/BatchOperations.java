package com.signscribe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BatchOperations {
	private static BatchOperations INSTANCE;

	public static BatchOperations getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new BatchOperations();
		}
		return INSTANCE;
	}

	public BatchResult batchRenameFiles(List<String> oldNames, List<String> newNames, Path txthDir) throws IOException {
		if (oldNames.size() != newNames.size()) {
			throw new IllegalArgumentException("Old names and new names must have same size");
		}

		BatchResult result = new BatchResult();
		List<String> successes = new ArrayList<>();
		List<String> failures = new ArrayList<>();
		List<String> skipped = new ArrayList<>();

		for (int i = 0; i < oldNames.size(); i++) {
			String oldName = oldNames.get(i);
			String newName = newNames.get(i);

			if (!SignScribeFileManager.getInstance().txthFileExists(oldName)) {
				skipped.add(oldName + " (not found)");
				continue;
			}

			if (SignScribeFileManager.getInstance().txthFileExists(newName)) {
				failures.add(oldName + " -> " + newName + " (target exists)");
				continue;
			}

			try {
				String content = SignScribeFileManager.getInstance().loadTxthFile(oldName);
				SignScribeFileManager.getInstance().saveTxthFile(newName, content);
				successes.add(oldName + " -> " + newName);
			} catch (IOException e) {
				failures.add(oldName + " -> " + newName + " (" + e.getMessage() + ")");
			}
		}

		result.successes = successes;
		result.failures = failures;
		result.skipped = skipped;
		return result;
	}

	public BatchResult batchDeleteFiles(List<String> filenames, Path txthDir) throws IOException {
		BatchResult result = new BatchResult();
		List<String> successes = new ArrayList<>();
		List<String> failures = new ArrayList<>();
		List<String> skipped = new ArrayList<>();

		for (String filename : filenames) {
			if (!SignScribeFileManager.getInstance().txthFileExists(filename)) {
				skipped.add(filename + " (not found)");
				continue;
			}

			try {
				Path filePath = txthDir.resolve(filename);
				java.nio.file.Files.delete(filePath);
				successes.add(filename);
			} catch (IOException e) {
				failures.add(filename + " (" + e.getMessage() + ")");
			}
		}

		result.successes = successes;
		result.failures = failures;
		result.skipped = skipped;
		return result;
	}

	public BatchResult batchCopyFiles(List<String> sourceFiles, List<String> targetFiles, Path txthDir) throws IOException {
		if (sourceFiles.size() != targetFiles.size()) {
			throw new IllegalArgumentException("Source files and target files must have same size");
		}

		BatchResult result = new BatchResult();
		List<String> successes = new ArrayList<>();
		List<String> failures = new ArrayList<>();
		List<String> skipped = new ArrayList<>();

		for (int i = 0; i < sourceFiles.size(); i++) {
			String sourceFile = sourceFiles.get(i);
			String targetFile = targetFiles.get(i);

			if (!SignScribeFileManager.getInstance().txthFileExists(sourceFile)) {
				skipped.add(sourceFile + " (not found)");
				continue;
			}

			try {
				String content = SignScribeFileManager.getInstance().loadTxthFile(sourceFile);
				SignScribeFileManager.getInstance().saveTxthFile(targetFile, content);
				successes.add(sourceFile + " -> " + targetFile);
			} catch (IOException e) {
				failures.add(sourceFile + " -> " + targetFile + " (" + e.getMessage() + ")");
			}
		}

		result.successes = successes;
		result.failures = failures;
		result.skipped = skipped;
		return result;
	}

	public BatchResult batchApplyTemplate(String templateName, List<String> targetFiles, Path txthDir) throws IOException {
		SignTemplate template = SignTemplateManager.getInstance().getTemplateByName(templateName);
		if (template == null) {
			throw new IOException("Template not found: " + templateName);
		}

		template.markAsUsed();
		SignTemplateManager.getInstance().saveTemplate(template);

		BatchResult result = new BatchResult();
		List<String> successes = new ArrayList<>();
		List<String> failures = new ArrayList<>();
		List<String> skipped = new ArrayList<>();

		for (String filename : targetFiles) {
			if (!SignScribeFileManager.getInstance().txthFileExists(filename)) {
				skipped.add(filename + " (not found)");
				continue;
			}

			try {
				String content = SignScribeFileManager.getInstance().loadTxthFile(filename);
				String[] lines = template.getLines();
				String newContent = String.format(
					"SIGN1:\n%s\n%s\n%s\n%s",
					lines[0], lines[1], lines[2], lines[3]
				);
				SignScribeFileManager.getInstance().saveTxthFile(filename, newContent);
				successes.add(filename);
			} catch (IOException e) {
				failures.add(filename + " (" + e.getMessage() + ")");
			}
		}

		result.successes = successes;
		result.failures = failures;
		result.skipped = skipped;
		return result;
	}

	public BatchResult batchReplaceTextInFiles(List<String> filenames, String searchText, String replaceText, Path txthDir) throws IOException {
		BatchResult result = new BatchResult();
		List<String> successes = new ArrayList<>();
		List<String> failures = new ArrayList<>();
		List<String> skipped = new ArrayList<>();
		List<String> modified = new ArrayList<>();

		for (String filename : filenames) {
			if (!SignScribeFileManager.getInstance().txthFileExists(filename)) {
				skipped.add(filename + " (not found)");
				continue;
			}

			try {
				String content = SignScribeFileManager.getInstance().loadTxthFile(filename);
				if (!content.contains(searchText)) {
					skipped.add(filename + " (text not found)");
					continue;
				}

				String newContent = content.replace(searchText, replaceText);
				if (newContent.equals(content)) {
					skipped.add(filename + " (no changes)");
					continue;
				}

				SignScribeFileManager.getInstance().saveTxthFile(filename, newContent);
				successes.add(filename);
				modified.add(filename);
			} catch (Exception e) {
				failures.add(filename + " (" + e.getMessage() + ")");
			}
		}

		result.successes = successes;
		result.failures = failures;
		result.skipped = skipped;
		return result;
	}

	public BatchResult batchValidateFiles(List<String> filenames, Path txthDir) throws IOException {
		BatchResult result = new BatchResult();
		List<String> valid = new ArrayList<>();
		List<String> invalid = new ArrayList<>();
		List<String> notFound = new ArrayList<>();

		for (String filename : filenames) {
			if (!SignScribeFileManager.getInstance().txthFileExists(filename)) {
				notFound.add(filename + " (not found)");
				continue;
			}

			try {
				Path filePath = txthDir.resolve(filename);
				TxthFileParser parser = new TxthFileParser(filePath);
				parser.parse();
				valid.add(filename);
			} catch (TxthParseException e) {
				invalid.add(filename + " (" + e.getMessage() + ")");
			} catch (IOException e) {
				invalid.add(filename + " (IO error: " + e.getMessage() + ")");
			}
		}

		result.successes = valid;
		result.failures = invalid;
		result.skipped = notFound;
		return result;
	}

	public static class BatchResult {
		public List<String> successes = new ArrayList<>();
		public List<String> failures = new ArrayList<>();
		public List<String> skipped = new ArrayList<>();

		public boolean hasFailures() {
			return !failures.isEmpty();
		}

		public boolean hasSkipped() {
			return !skipped.isEmpty();
		}

		public int getTotalOperations() {
			return successes.size() + failures.size() + skipped.size();
		}

		public int getSuccessCount() {
			return successes.size();
		}

		public int getFailureCount() {
			return failures.size();
		}

		public int getSkippedCount() {
			return skipped.size();
		}

		public String getSummary() {
			return String.format("Total: %d | Success: %d | Failed: %d | Skipped: %d",
				getTotalOperations(), getSuccessCount(), getFailureCount(), getSkippedCount());
		}
	}
}
