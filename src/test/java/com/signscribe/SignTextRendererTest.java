package com.signscribe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SignTextRendererTest {
	
	private SignPage testPage;
	private SignPage emptyPage;
	
	@BeforeEach
	void setUp() {
		testPage = new SignPage(1, "Line 1", "Line 2", "Line 3", "Line 4");
		emptyPage = new SignPage(0, "", "", "", "");
	}
	
	@Test
	void testSignPageCreation() {
		assertEquals("Line 1", testPage.getLines()[0]);
		assertEquals("Line 2", testPage.getLines()[1]);
		assertEquals("Line 3", testPage.getLines()[2]);
		assertEquals("Line 4", testPage.getLines()[3]);
	}
	
	@Test
	void testEmptySignPage() {
		assertEquals("", emptyPage.getLines()[0]);
		assertEquals("", emptyPage.getLines()[1]);
		assertEquals("", emptyPage.getLines()[2]);
		assertEquals("", emptyPage.getLines()[3]);
	}
	
	@Test
	void testSignPageLength() {
		String[] lines = testPage.getLines();
		assertEquals(4, lines.length);
	}
	
	@Test
	void testSignPageWithBlankLines() {
		SignPage page = new SignPage(2, "Text 1", "", "Text 3", "");
		assertEquals("Text 1", page.getLines()[0]);
		assertEquals("", page.getLines()[1]);
		assertEquals("Text 3", page.getLines()[2]);
		assertEquals("", page.getLines()[3]);
	}
	
	@Test
	void testSignPageSignNumber() {
		assertEquals(1, testPage.getSignNumber());
		assertEquals(0, emptyPage.getSignNumber());
		
		SignPage page3 = new SignPage(42, "A", "B", "C", "D");
		assertEquals(42, page3.getSignNumber());
	}
}
