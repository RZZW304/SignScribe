package com.signscribe;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtSizeTracker;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class SignScribeData {
	private static SignScribeData INSTANCE;
	private Path dataFile;

	private String currentTxthFile = "";
	private int currentPageIndex = 0;
	private int totalSigns = 0;
	private boolean hasActiveSession = false;

	public static SignScribeData getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignScribeData();
		}
		return INSTANCE;
	}

	public void setDataFile(Path configDir) {
		this.dataFile = configDir.resolve("signscribe").resolve("data.dat");
	}

	public void load() throws IOException {
		if (dataFile == null || !dataFile.toFile().exists()) {
			return;
		}
		NbtCompound nbt = NbtIo.readCompressed(dataFile, NbtSizeTracker.ofUnlimitedBytes());
		if (nbt == null) {
			return;
		}
		currentTxthFile = nbt.getString("currentTxthFile");
		currentPageIndex = nbt.getInt("currentPageIndex");
		totalSigns = nbt.getInt("totalSigns");
		hasActiveSession = nbt.getBoolean("hasActiveSession");
	}

	public void save() throws IOException {
		if (dataFile == null) {
			return;
		}
		NbtCompound nbt = new NbtCompound();
		nbt.putString("currentTxthFile", currentTxthFile);
		nbt.putInt("currentPageIndex", currentPageIndex);
		nbt.putInt("totalSigns", totalSigns);
		nbt.putBoolean("hasActiveSession", hasActiveSession);
		NbtIo.writeCompressed(nbt, dataFile);
	}

	public String getCurrentTxthFile() {
		return currentTxthFile;
	}

	public void setCurrentTxthFile(String currentTxthFile) {
		this.currentTxthFile = currentTxthFile;
	}

	public int getCurrentPageIndex() {
		return currentPageIndex;
	}

	public void setCurrentPageIndex(int currentPageIndex) {
		this.currentPageIndex = currentPageIndex;
	}

	public int getTotalSigns() {
		return totalSigns;
	}

	public void setTotalSigns(int totalSigns) {
		this.totalSigns = totalSigns;
	}

	public boolean hasActiveSession() {
		return hasActiveSession;
	}

	public void setHasActiveSession(boolean hasActiveSession) {
		this.hasActiveSession = hasActiveSession;
	}

	public void resetSession() {
		currentTxthFile = "";
		currentPageIndex = 0;
		totalSigns = 0;
		hasActiveSession = false;
	}
}
