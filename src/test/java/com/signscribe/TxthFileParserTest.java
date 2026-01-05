package com.signscribe;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class TxthFileParserTest {
	public static void main(String[] args) {
		System.out.println("=== TxthFileParser Test Suite ===\n");

		testValidFile();
		testInvalidEmptyLines();
		testInvalidLineCount();
		testInvalidLineLength();

		System.out.println("\n=== Test Suite Complete ===");
	}

	private static void testValidFile() {
		System.out.println("Test 1: Valid .txth file");
		Path path = Paths.get("test-files/valid.txth");

		try {
			TxthFileParser parser = new TxthFileParser(path);
			List<SignPage> pages = parser.parse();

			System.out.println("✓ File parsed successfully");
			System.out.println("  Total signs: " + parser.getTotalSigns());

			for (SignPage page : pages) {
				System.out.println("  SIGN" + page.getSignNumber() + ":");
				for (int i = 0; i < 4; i++) {
					String line = page.getLine(i);
					System.out.println("    " + (i + 1) + ": \"" + line + "\" (" + line.length() + " chars)");
				}
			}
		} catch (IOException e) {
			System.out.println("✗ IO Error: " + e.getMessage());
		} catch (TxthParseException e) {
			System.out.println("✗ Parse Error: " + e.getMessage());
		}
		System.out.println();
	}

	private static void testInvalidEmptyLines() {
		System.out.println("Test 2: Invalid .txth file (empty lines)");
		Path path = Paths.get("test-files/invalid-empty-lines.txth");

		try {
			TxthFileParser parser = new TxthFileParser(path);
			parser.parse();
			System.out.println("✗ Expected parse error but got none");
		} catch (IOException e) {
			System.out.println("✗ IO Error: " + e.getMessage());
		} catch (TxthParseException e) {
			System.out.println("✓ Parse error detected (expected): " + e.getMessage());
		}
		System.out.println();
	}

	private static void testInvalidLineCount() {
		System.out.println("Test 3: Invalid .txth file (wrong line count)");
		Path path = Paths.get("test-files/invalid-line-count.txth");

		try {
			TxthFileParser parser = new TxthFileParser(path);
			parser.parse();
			System.out.println("✗ Expected parse error but got none");
		} catch (IOException e) {
			System.out.println("✗ IO Error: " + e.getMessage());
		} catch (TxthParseException e) {
			System.out.println("✓ Parse error detected (expected): " + e.getMessage());
		}
		System.out.println();
	}

	private static void testInvalidLineLength() {
		System.out.println("Test 4: Invalid .txth file (wrong line length)");
		Path path = Paths.get("test-files/invalid-line-length.txth");

		try {
			TxthFileParser parser = new TxthFileParser(path);
			parser.parse();
			System.out.println("✗ Expected parse error but got none");
		} catch (IOException e) {
			System.out.println("✗ IO Error: " + e.getMessage());
		} catch (TxthParseException e) {
			System.out.println("✓ Parse error detected (expected): " + e.getMessage());
		}
		System.out.println();
	}
}
