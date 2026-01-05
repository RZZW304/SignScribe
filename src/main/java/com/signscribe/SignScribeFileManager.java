package com.signscribe;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SignScribeFileManager {
	private static SignScribeFileManager INSTANCE;
	private Path configDir;

	public static SignScribeFileManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignScribeFileManager();
		}
		return INSTANCE;
	}

	public void setConfigDir(Path configDir) {
		this.configDir = configDir.resolve("signscribe");
		try {
			if (!Files.exists(this.configDir)) {
				Files.createDirectories(this.configDir);
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to create SignScribe config directory", e);
		}
	}

	public Path getConfigDir() {
		return configDir;
	}

	public Path getTxthDirectory() {
		return configDir.resolve("txth");
	}

	public void initializeDirectories() throws IOException {
		Path txthDir = getTxthDirectory();
		if (!Files.exists(txthDir)) {
			Files.createDirectories(txthDir);
		}
	}

	public String loadTxthFile(String filename) throws IOException {
		Path file = resolveTxthPath(filename);
		if (!Files.exists(file)) {
			throw new IOException("File not found: " + filename);
		}
		return Files.readString(file);
	}

	public Path resolveTxthPath(String path) {
		path = path.trim();

		if (path.startsWith("config/") || path.startsWith("./config/") || path.startsWith(".\\config\\")) {
			return configDir.resolve(path.replaceFirst("^config(/|\\\\)", ""));
		}

		if (path.startsWith("/")) {
			return configDir.resolve(path.substring(1));
		}

		if (path.contains("/") || path.contains("\\")) {
			return configDir.resolve(path);
		}

		return getTxthDirectory().resolve(path);
	}

	public List<String> listTxthFiles() throws IOException {
		Path txthDir = getTxthDirectory();
		if (!Files.exists(txthDir)) {
			return new ArrayList<>();
		}
		try (Stream<Path> paths = Files.walk(txthDir, 1)) {
			return paths
				.filter(Files::isRegularFile)
				.filter(p -> p.toString().endsWith(".txth"))
				.map(p -> p.getFileName().toString())
				.collect(Collectors.toList());
		}
	}

	public boolean txthFileExists(String filename) {
		Path txthDir = getTxthDirectory();
		Path file = txthDir.resolve(filename);
		return Files.exists(file);
	}

	public void saveTxthFile(String filename, String content) throws IOException {
		Path txthDir = getTxthDirectory();
		Path file = txthDir.resolve(filename);
		Files.writeString(file, content);
	}
}
