package com.signscribe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SignScribeDataTest {
	
	@TempDir
	Path tempDir;
	
	private SignScribeData data;
	
	@BeforeEach
	void setUp() {
		data = SignScribeData.getInstance();
		data.setDataFile(tempDir);
		data.resetSession();
	}
	
	@Test
	void testInitialValues() {
		assertEquals("", data.getCurrentTxthFile());
		assertEquals(0, data.getCurrentPageIndex());
		assertEquals(0, data.getTotalSigns());
		assertFalse(data.hasActiveSession());
	}
	
	@Test
	void testSettersAndGetters() {
		data.setCurrentTxthFile("test.txth");
		assertEquals("test.txth", data.getCurrentTxthFile());
		
		data.setCurrentPageIndex(5);
		assertEquals(5, data.getCurrentPageIndex());
		
		data.setTotalSigns(10);
		assertEquals(10, data.getTotalSigns());
		
		data.setHasActiveSession(true);
		assertTrue(data.hasActiveSession());
	}
	
	@Test
	void testSaveAndLoad() throws IOException {
		data.setCurrentTxthFile("saved.txth");
		data.setCurrentPageIndex(3);
		data.setTotalSigns(7);
		data.setHasActiveSession(true);
		
		data.save();
		
		SignScribeData newData = SignScribeData.getInstance();
		newData.setDataFile(tempDir);
		newData.load();
		
		assertEquals("saved.txth", newData.getCurrentTxthFile());
		assertEquals(3, newData.getCurrentPageIndex());
		assertEquals(7, newData.getTotalSigns());
		assertTrue(newData.hasActiveSession());
	}
	
	@Test
	void testResetSession() {
		data.setCurrentTxthFile("test.txth");
		data.setCurrentPageIndex(2);
		data.setTotalSigns(5);
		data.setHasActiveSession(true);
		
		data.resetSession();
		
		assertEquals("", data.getCurrentTxthFile());
		assertEquals(0, data.getCurrentPageIndex());
		assertEquals(0, data.getTotalSigns());
		assertFalse(data.hasActiveSession());
	}
	
	@Test
	void testLoadNonExistentFile() throws IOException {
		assertDoesNotThrow(() -> data.load());
	}
}
