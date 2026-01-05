package com.signscribe;

public class TxthParseException extends Exception {
	private final int signNumber;
	private final int lineNumber;
	private final String details;

	public TxthParseException(String message, int signNumber, int lineNumber, String details) {
		super(message);
		this.signNumber = signNumber;
		this.lineNumber = lineNumber;
		this.details = details;
	}

	public int getSignNumber() {
		return signNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public String getDetails() {
		return details;
	}

	@Override
	public String getMessage() {
		return String.format("Error in SIGN%d at line %d: %s (%s)", signNumber, lineNumber, super.getMessage(), details);
	}
}
