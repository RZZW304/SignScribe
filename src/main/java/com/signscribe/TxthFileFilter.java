package com.signscribe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TxthFileFilter {
	private final List<TxthFileInfo> allFiles;

	public TxthFileFilter(List<String> filenames, Path txthDir) throws IOException {
		this.allFiles = new ArrayList<>();
		for (String filename : filenames) {
			Path filePath = txthDir.resolve(filename);
			BasicFileAttributes attrs = Files.readAttributes(filePath, BasicFileAttributes.class);
			FileTime createdTime = attrs.creationTime();
			FileTime modifiedTime = attrs.lastModifiedTime();
			long fileSize = attrs.size();

			allFiles.add(new TxthFileInfo(filename, createdTime.toMillis(), modifiedTime.toMillis(), fileSize));
		}
	}

	public List<TxthFileInfo> filterByName(String searchTerm) {
		String lowerSearch = searchTerm.toLowerCase();
		return allFiles.stream()
			.filter(f -> f.name.toLowerCase().contains(lowerSearch))
			.collect(Collectors.toList());
	}

	public List<TxthFileInfo> filterByNameExact(String exactName) {
		return allFiles.stream()
			.filter(f -> f.name.equals(exactName))
			.collect(Collectors.toList());
	}

	public List<TxthFileInfo> filterByExtension(String extension) {
		return allFiles.stream()
			.filter(f -> f.name.endsWith(extension))
			.collect(Collectors.toList());
	}

	public List<TxthFileInfo> filterByCreatedAfter(long timestamp) {
		return allFiles.stream()
			.filter(f -> f.createdTime >= timestamp)
			.collect(Collectors.toList());
	}

	public List<TxthFileInfo> filterByCreatedBefore(long timestamp) {
		return allFiles.stream()
			.filter(f -> f.createdTime <= timestamp)
			.collect(Collectors.toList());
	}

	public List<TxthFileInfo> filterByModifiedAfter(long timestamp) {
		return allFiles.stream()
			.filter(f -> f.modifiedTime >= timestamp)
			.collect(Collectors.toList());
	}

	public List<TxthFileInfo> filterByModifiedBefore(long timestamp) {
		return allFiles.stream()
			.filter(f -> f.modifiedTime <= timestamp)
			.collect(Collectors.toList());
	}

	public List<TxthFileInfo> filterBySizeMin(long minBytes) {
		return allFiles.stream()
			.filter(f -> f.size >= minBytes)
			.collect(Collectors.toList());
	}

	public List<TxthFileInfo> filterBySizeMax(long maxBytes) {
		return allFiles.stream()
			.filter(f -> f.size <= maxBytes)
			.collect(Collectors.toList());
	}

	public List<TxthFileInfo> sortBy(SortCriteria criteria, SortOrder order) {
		List<TxthFileInfo> sorted = new ArrayList<>(allFiles);
		Comparator<TxthFileInfo> comparator = null;

		switch (criteria) {
			case NAME:
				comparator = Comparator.comparing(f -> f.name.toLowerCase());
				break;
			case CREATED:
				comparator = Comparator.comparing(f -> f.createdTime);
				break;
			case MODIFIED:
				comparator = Comparator.comparing(f -> f.modifiedTime);
				break;
			case SIZE:
				comparator = Comparator.comparing(f -> f.size);
				break;
		}

		if (order == SortOrder.DESCENDING) {
			comparator = comparator.reversed();
		}

		sorted.sort(comparator);
		return sorted;
	}

	public List<TxthFileInfo> filterAndSort(Predicate<TxthFileInfo> filter, SortCriteria criteria, SortOrder order) {
		List<TxthFileInfo> filtered = allFiles.stream()
			.filter(filter)
			.collect(Collectors.toList());

		Comparator<TxthFileInfo> comparator = null;
		switch (criteria) {
			case NAME:
				comparator = Comparator.comparing(f -> f.name.toLowerCase());
				break;
			case CREATED:
				comparator = Comparator.comparing(f -> f.createdTime);
				break;
			case MODIFIED:
				comparator = Comparator.comparing(f -> f.modifiedTime);
				break;
			case SIZE:
				comparator = Comparator.comparing(f -> f.size);
				break;
		}

		if (order == SortOrder.DESCENDING) {
			comparator = comparator.reversed();
		}

		filtered.sort(comparator);
		return filtered;
	}

	public List<TxthFileInfo> getRecentFiles(int days) {
		long cutoff = System.currentTimeMillis() - (days * 24L * 60L * 60L * 1000L);
		return filterByModifiedAfter(cutoff);
	}

	public List<TxthFileInfo> getLargestFiles(int limit) {
		return allFiles.stream()
			.sorted((a, b) -> Long.compare(b.size, a.size))
			.limit(limit)
			.collect(Collectors.toList());
	}

	public List<TxthFileInfo> getSmallestFiles(int limit) {
		return allFiles.stream()
			.sorted((a, b) -> Long.compare(a.size, b.size))
			.limit(limit)
			.collect(Collectors.toList());
	}

	public List<String> getGroupedByExtension() {
		Map<String, Integer> groups = new HashMap<>();
		for (TxthFileInfo file : allFiles) {
			String ext = file.name.substring(file.name.lastIndexOf('.') + 1).toLowerCase();
			groups.put(ext, groups.getOrDefault(ext, 0) + 1);
		}
		List<String> result = new ArrayList<>();
		for (Map.Entry<String, Integer> entry : groups.entrySet()) {
			result.add(entry.getKey() + ": " + entry.getValue() + " files");
		}
		Collections.sort(result);
		return result;
	}

	public int getTotalFiles() {
		return allFiles.size();
	}

	public long getTotalSize() {
		return allFiles.stream()
			.mapToLong(f -> f.size)
			.sum();
	}

	public enum SortCriteria {
		NAME, CREATED, MODIFIED, SIZE
	}

	public enum SortOrder {
		ASCENDING, DESCENDING
	}

	public static class TxthFileInfo {
		public final String name;
		public final long createdTime;
		public final long modifiedTime;
		public final long size;

		public TxthFileInfo(String name, long createdTime, long modifiedTime, long size) {
			this.name = name;
			this.createdTime = createdTime;
			this.modifiedTime = modifiedTime;
			this.size = size;
		}

		public String getSizeFormatted() {
			if (size < 1024) {
				return size + " B";
			} else if (size < 1024 * 1024) {
				return String.format("%.1f KB", size / 1024.0);
			} else {
				return String.format("%.2f MB", size / (1024.0 * 1024.0));
			}
		}

		public String getCreatedFormatted() {
			Date date = new Date(createdTime);
			return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		}

		public String getModifiedFormatted() {
			Date date = new Date(modifiedTime);
			return new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
		}
	}
}
