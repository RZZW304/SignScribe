package com.signscribe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class SignScribePlacementTest {
	
	@TempDir
	Path tempDir;
	
	private SignScribePlacement placement;
	private SignScribeFileManager fileManager;
	
	@BeforeEach
	void setUp() throws IOException {
		placement = SignScribePlacement.getInstance();
		fileManager = SignScribeFileManager.getInstance();
		fileManager.setConfigDir(tempDir);
		fileManager.initializeDirectories();
		SignScribeData.getInstance().setDataFile(tempDir);
	}
	
	@Test
	void testInitialState() {
		assertFalse(placement.hasSession());
		assertNull(placement.getCurrentSignPage());
		assertNull(placement.getNextSignPage());
		assertFalse(placement.hasNextPage());
		assertFalse(placement.hasPreviousPage());
	}
	
	@Test
	void testStartSession() throws Exception {
		String content = "SIGN1:\nLine 1\nLine 2\nLine 3\nLine 4\n\nSIGN2:\nLine 5\nLine 6\nLine 7\nLine 8";
		fileManager.saveTxthFile("test.txth", content);
		
		placement.startSession("test.txth");
		
		assertTrue(placement.hasSession());
		assertEquals("test.txth", placement.getCurrentFilename());
		assertEquals(0, placement.getCurrentPageIndex());
		assertEquals(2, placement.getTotalSigns());
		assertNotNull(placement.getCurrentSignPage());
	}
	
	@Test
	void testGetCurrentSignPage() throws Exception {
		String content = "SIGN1:\nA\nB\nC\nD";
		fileManager.saveTxthFile("test.txth", content);
		
		placement.startSession("test.txth");
		SignPage page = placement.getCurrentSignPage();
		
		assertNotNull(page);
		assertEquals("A", page.getLines()[0]);
		assertEquals("D", page.getLines()[3]);
	}
	
	@Test
	void testGetNextSignPage() throws Exception {
		String content = "SIGN1:\nA\nB\nC\nD\n\nSIGN2:\nE\nF\nG\nH";
		fileManager.saveTxthFile("test.txth", content);
		
		placement.startSession("test.txth");
		SignPage next = placement.getNextSignPage();
		
		assertNotNull(next);
		assertEquals("E", next.getLines()[0]);
	}
	
	@Test
	void testAdvanceToNextPage() throws Exception {
		String content = "SIGN1:\nA\nB\nC\nD\n\nSIGN2:\nE\nF\nG\nH";
		fileManager.saveTxthFile("test.txth", content);
		
		placement.startSession("test.txth");
		placement.advanceToNextPage();
		
		assertEquals(1, placement.getCurrentPageIndex());
		SignPage page = placement.getCurrentSignPage();
		assertEquals("E", page.getLines()[0]);
	}
	
	@Test
	void testAdvancePastLastPage() throws Exception {
		String content = "SIGN1:\nA\nB\nC\nD";
		fileManager.saveTxthFile("test.txth", content);
		
		placement.startSession("test.txth");
		placement.advanceToNextPage();
		
		assertFalse(placement.hasSession());
	}
	
	@Test
	void testGoToPage() throws Exception {
		String content = "SIGN1:\nA\nB\nC\nD\n\nSIGN2:\nE\nF\nG\nH\n\nSIGN3:\nI\nJ\nK\nL";
		fileManager.saveTxthFile("test.txth", content);
		
		placement.startSession("test.txth");
		placement.goToPage(2);
		
		assertEquals(2, placement.getCurrentPageIndex());
		SignPage page = placement.getCurrentSignPage();
		assertEquals("I", page.getLines()[0]);
	}
	
	@Test
	void testEndSession() throws Exception {
		String content = "SIGN1:\nA\nB\nC\nD";
		fileManager.saveTxthFile("test.txth", content);
		
		placement.startSession("test.txth");
		placement.endSession();
		
		assertFalse(placement.hasSession());
		assertNull(placement.getCurrentSignPage());
	}
	
	@Test
	void testHasNextPage() throws Exception {
		String content = "SIGN1:\nA\nB\nC\nD\n\nSIGN2:\nE\nF\nG\nH";
		fileManager.saveTxthFile("test.txth", content);
		
		placement.startSession("test.txth");
		assertTrue(placement.hasNextPage());
		
		placement.advanceToNextPage();
		assertFalse(placement.hasNextPage());
	}
	
	@Test
	void testHasPreviousPage() throws Exception {
		String content = "SIGN1:\nA\nB\nC\nD\n\nSIGN2:\nE\nF\nG\nH";
		fileManager.saveTxthFile("test.txth", content);
		
		placement.startSession("test.txth");
		assertFalse(placement.hasPreviousPage());
		
		placement.advanceToNextPage();
		assertTrue(placement.hasPreviousPage());
	}
}
