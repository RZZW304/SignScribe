package com.signscribe;

import java.util.ArrayList;
import java.util.List;

public class SignPage {
	private final String[] lines;
	private final int signNumber;

	public SignPage(int signNumber, String line1, String line2, String line3, String line4) {
		this.signNumber = signNumber;
		this.lines = new String[] { line1, line2, line3, line4 };
	}

	public String[] getLines() {
		return lines;
	}

	public int getSignNumber() {
		return signNumber;
	}

	public String getLine(int index) {
		return lines[index];
	}
}
