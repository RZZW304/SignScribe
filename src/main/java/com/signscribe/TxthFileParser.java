package com.signscribe;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TxthFileParser {
	private static final int LINES_PER_SIGN = 4;
	private static final int CHARS_PER_LINE = 14;
	private static final Pattern SIGN_MARKER_PATTERN = Pattern.compile("^SIGN(\\d+):\\s*$");
	private static final String BLANK_MARKER = "{BLANk}";

	private Path filePath;
	private List<SignPage> signPages;
	private int totalSigns;

	public TxthFileParser(Path filePath) {
		this.filePath = filePath;
		this.signPages = new ArrayList<>();
		this.totalSigns = 0;
	}

	public List<SignPage> parse() throws IOException, TxthParseException {
		List<String> lines = readAllLines();
		parseLines(lines);
		return signPages;
	}

	private List<String> readAllLines() throws IOException {
		return Files.readAllLines(filePath, StandardCharsets.UTF_8);
	}

	private void parseLines(List<String> lines) throws TxthParseException {
		int currentSignNumber = 0;
		List<String> currentSignLines = new ArrayList<>();
		int lineNumber = 0;

		for (String line : lines) {
			lineNumber++;
			String trimmedLine = line.trim();

			Matcher matcher = SIGN_MARKER_PATTERN.matcher(trimmedLine);

			if (matcher.matches()) {
				int signNumber = Integer.parseInt(matcher.group(1));

				if (currentSignNumber > 0 && !currentSignLines.isEmpty()) {
					processSignBlock(currentSignNumber, currentSignLines);
				}

				currentSignNumber = signNumber;
				currentSignLines.clear();
			} else if (currentSignNumber > 0) {
				if (currentSignLines.size() < LINES_PER_SIGN) {
					currentSignLines.add(line);
				} else {
					throw new TxthParseException(
						"Extra line in sign block",
						currentSignNumber,
						currentSignLines.size() + 1,
						"Found line: " + line
					);
				}
			}
		}

		if (currentSignNumber > 0 && !currentSignLines.isEmpty()) {
			processSignBlock(currentSignNumber, currentSignLines);
		}
	}

	private void processSignBlock(int signNumber, List<String> lines) throws TxthParseException {
		if (lines.size() != LINES_PER_SIGN) {
			throw new TxthParseException(
				"Expected exactly " + LINES_PER_SIGN + " lines but found " + lines.size(),
				signNumber,
				lines.size(),
				"Each sign must have exactly 4 lines"
			);
		}

		String[] processedLines = new String[LINES_PER_SIGN];

		for (int i = 0; i < LINES_PER_SIGN; i++) {
			String line = lines.get(i);

			if (line.isEmpty()) {
				throw new TxthParseException(
					"Empty line detected (use {BLANk} for intentional blank lines)",
					signNumber,
					i + 1,
					"Empty lines are not allowed without {BLANk} marker"
				);
			}

			if (line.equals(BLANK_MARKER)) {
				processedLines[i] = "";
			} else {
				if (line.length() != CHARS_PER_LINE) {
					throw new TxthParseException(
						"Expected exactly " + CHARS_PER_LINE + " characters but found " + line.length(),
						signNumber,
						i + 1,
						"Line content: \"" + line + "\""
					);
				}
				processedLines[i] = line;
			}
		}

		SignPage signPage = new SignPage(signNumber, processedLines[0], processedLines[1], processedLines[2], processedLines[3]);
		signPages.add(signPage);
		totalSigns++;
	}

	public List<SignPage> getSignPages() {
		return signPages;
	}

	public int getTotalSigns() {
		return totalSigns;
	}
}
