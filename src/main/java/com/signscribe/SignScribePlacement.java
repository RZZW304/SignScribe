package com.signscribe;

import java.io.IOException;
import java.util.List;

public class SignScribePlacement {
	private static SignScribePlacement INSTANCE;
	
	private List<SignPage> signPages;
	private boolean sessionActive = false;
	
	public static SignScribePlacement getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignScribePlacement();
		}
		return INSTANCE;
	}
	
	public void startSession(String filename) throws IOException, TxthParseException {
		SignScribeFileManager fileManager = SignScribeFileManager.getInstance();
		SignScribeData data = SignScribeData.getInstance();
		
		java.nio.file.Path filePath = fileManager.getTxthDirectory().resolve(filename);
		TxthFileParser parser = new TxthFileParser(filePath);
		signPages = parser.parse();
		
		data.setCurrentTxthFile(filename);
		data.setTotalSigns(signPages.size());
		data.setCurrentPageIndex(0);
		data.setHasActiveSession(true);
		data.save();
		
		sessionActive = true;
	}
	
	public void endSession() throws IOException {
		SignScribeData data = SignScribeData.getInstance();
		data.resetSession();
		data.save();
		
		sessionActive = false;
		signPages = null;
	}
	
	public SignPage getCurrentSignPage() {
		if (!sessionActive || signPages == null) {
			return null;
		}
		SignScribeData data = SignScribeData.getInstance();
		int index = data.getCurrentPageIndex();
		if (index >= 0 && index < signPages.size()) {
			return signPages.get(index);
		}
		return null;
	}
	
	public SignPage getNextSignPage() {
		if (!sessionActive || signPages == null) {
			return null;
		}
		SignScribeData data = SignScribeData.getInstance();
		int nextIndex = data.getCurrentPageIndex() + 1;
		if (nextIndex < signPages.size()) {
			return signPages.get(nextIndex);
		}
		return null;
	}
	
	public void advanceToNextPage() throws IOException {
		if (!sessionActive || signPages == null) {
			return;
		}
		SignScribeData data = SignScribeData.getInstance();
		int nextIndex = data.getCurrentPageIndex() + 1;
		if (nextIndex < signPages.size()) {
			data.setCurrentPageIndex(nextIndex);
			data.save();
		} else {
			endSession();
		}
	}
	
	public void goToPage(int pageIndex) throws IOException {
		if (!sessionActive || signPages == null) {
			return;
		}
		if (pageIndex >= 0 && pageIndex < signPages.size()) {
			SignScribeData data = SignScribeData.getInstance();
			data.setCurrentPageIndex(pageIndex);
			data.save();
		}
	}
	
	public int getCurrentPageIndex() {
		SignScribeData data = SignScribeData.getInstance();
		return data.getCurrentPageIndex();
	}
	
	public int getTotalSigns() {
		SignScribeData data = SignScribeData.getInstance();
		return data.getTotalSigns();
	}
	
	public boolean hasSession() {
		return sessionActive;
	}
	
	public String getCurrentFilename() {
		SignScribeData data = SignScribeData.getInstance();
		return data.getCurrentTxthFile();
	}
	
	public boolean hasNextPage() {
		if (!sessionActive || signPages == null) {
			return false;
		}
		SignScribeData data = SignScribeData.getInstance();
		return data.getCurrentPageIndex() + 1 < signPages.size();
	}
	
	public boolean hasPreviousPage() {
		if (!sessionActive) {
			return false;
		}
		SignScribeData data = SignScribeData.getInstance();
		return data.getCurrentPageIndex() > 0;
	}
}
