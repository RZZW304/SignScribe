package com.signscribe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SignScribeFileManagerTest {
	
	@TempDir
	Path tempDir;
	
	private SignScribeFileManager fileManager;
	
	@BeforeEach
	void setUp() {
		fileManager = SignScribeFileManager.getInstance();
		fileManager.setConfigDir(tempDir);
	}
	
	@Test
	void testInitializeDirectories() throws IOException {
		fileManager.initializeDirectories();
		
		Path txthDir = fileManager.getTxthDirectory();
		assertTrue(Files.exists(txthDir));
		assertTrue(Files.isDirectory(txthDir));
	}
	
	@Test
	void testSaveAndLoadTxthFile() throws IOException {
		String filename = "test.txth";
		String content = "SIGN1:\nLine 1\nLine 2\nLine 3\nLine 4";
		
		fileManager.initializeDirectories();
		fileManager.saveTxthFile(filename, content);
		
		assertTrue(fileManager.txthFileExists(filename));
		
		String loaded = fileManager.loadTxthFile(filename);
		assertEquals(content, loaded);
	}
	
	@Test
	void testListTxthFiles() throws IOException {
		fileManager.initializeDirectories();
		
		assertEquals(0, fileManager.listTxthFiles().size());
		
		fileManager.saveTxthFile("file1.txth", "SIGN1:\nA\nB\nC\nD");
		fileManager.saveTxthFile("file2.txth", "SIGN1:\nE\nF\nG\nH");
		Files.writeString(fileManager.getTxthDirectory().resolve("not-txth.txt"), "test");
		
		List<String> files = fileManager.listTxthFiles();
		assertEquals(2, files.size());
		assertTrue(files.contains("file1.txth"));
		assertTrue(files.contains("file2.txth"));
		assertFalse(files.contains("not-txth.txt"));
	}
	
	@Test
	void testTxthFileExists() throws IOException {
		fileManager.initializeDirectories();
		
		assertFalse(fileManager.txthFileExists("nonexistent.txth"));
		
		fileManager.saveTxthFile("exists.txth", "SIGN1:\nA\nB\nC\nD");
		
		assertTrue(fileManager.txthFileExists("exists.txth"));
	}
	
	@Test
	void testLoadNonExistentFile() throws IOException {
		fileManager.initializeDirectories();
		
		assertThrows(IOException.class, () -> {
			fileManager.loadTxthFile("doesnotexist.txth");
		});
	}
}
