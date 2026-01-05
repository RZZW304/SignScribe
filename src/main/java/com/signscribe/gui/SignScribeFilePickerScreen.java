package com.signscribe.gui;

import com.signscribe.SignScribeFileManager;
import com.signscribe.SignScribePlacement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class SignScribeFilePickerScreen extends Screen {
	private static final int BUTTON_WIDTH = 250;
	private static final int BUTTON_HEIGHT = 24;
	private static final int BUTTON_PADDING = 4;
	
	private final Screen parent;
	private Path currentPath;
	private List<FileEntry> entries;
	private int scrollOffset = 0;
	private int maxVisibleItems;
	private String errorMessage = "";
	
	public SignScribeFilePickerScreen(Screen parent) {
		super(Text.of("SignScribe File Picker"));
		this.parent = parent;
		this.currentPath = SignScribeFileManager.getInstance().getConfigDir().resolve("signscribe");
		loadEntries();
	}
	
	private static class FileEntry {
		final String name;
		final Path path;
		final boolean isDirectory;
		final boolean isTxthFile;
		
		FileEntry(String name, Path path, boolean isDirectory, boolean isTxthFile) {
			this.name = name;
			this.path = path;
			this.isDirectory = isDirectory;
			this.isTxthFile = isTxthFile;
		}
	}
	
	private void loadEntries() {
		entries = new ArrayList<>();
		
		try {
			File currentDir = currentPath.toFile();
			if (!currentDir.exists() || !currentDir.isDirectory()) {
				errorMessage = "Directory not found: " + currentPath;
				return;
			}
			
			Collection<File> files = FileUtils.listFilesAndDirs(
				currentDir,
				TrueFileFilter.INSTANCE,
				TrueFileFilter.INSTANCE
			);
			
			for (File file : files) {
				if (file.equals(currentDir)) continue;
				
				String name = file.getName();
				if (name.startsWith(".")) continue;
				
				boolean isDirectory = file.isDirectory();
				boolean isTxthFile = file.isFile() && name.toLowerCase().endsWith(".txth");
				
				if (isDirectory || isTxthFile) {
					entries.add(new FileEntry(name, file.toPath(), isDirectory, isTxthFile));
				}
			}
			
			entries.sort(Comparator.comparing((FileEntry e) -> !e.isDirectory)
				.thenComparing(e -> e.name.toLowerCase()));
			
		} catch (Exception e) {
			errorMessage = "Error loading files: " + e.getMessage();
			System.err.println("[SignScribe] Error loading files: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	@Override
	protected void init() {
		super.init();
		
		maxVisibleItems = (height - 100) / (BUTTON_HEIGHT + BUTTON_PADDING);
		
		int buttonX = (width - BUTTON_WIDTH) / 2;
		int startY = 60;
		
		int visibleCount = Math.min(entries.size() - scrollOffset, maxVisibleItems);
		for (int i = 0; i < visibleCount; i++) {
			FileEntry entry = entries.get(scrollOffset + i);
			int y = startY + i * (BUTTON_HEIGHT + BUTTON_PADDING);
			
			String buttonText = entry.isDirectory ? "[DIR] " + entry.name : entry.name;
			if (entry.isTxthFile) {
				buttonText = "[TXTH] " + entry.name;
			}
			
			this.addDrawableChild(ButtonWidget.builder(
				Text.of(buttonText),
				button -> handleEntryClick(entry)
			).dimensions(buttonX, y, BUTTON_WIDTH, BUTTON_HEIGHT).build());
		}
		
		if (scrollOffset > 0) {
			this.addDrawableChild(ButtonWidget.builder(
				Text.of("▲ Scroll Up"),
				button -> {
					scrollOffset = Math.max(0, scrollOffset - maxVisibleItems);
					clearAndInit();
				}
			).dimensions(buttonX + BUTTON_WIDTH + 10, startY, 100, BUTTON_HEIGHT).build());
		}
		
		if (scrollOffset + maxVisibleItems < entries.size()) {
			int downButtonY = startY + (maxVisibleItems - 1) * (BUTTON_HEIGHT + BUTTON_PADDING);
			this.addDrawableChild(ButtonWidget.builder(
				Text.of("▼ Scroll Down"),
				button -> {
					scrollOffset = Math.min(entries.size() - maxVisibleItems, scrollOffset + maxVisibleItems);
					clearAndInit();
				}
			).dimensions(buttonX + BUTTON_WIDTH + 10, downButtonY, 100, BUTTON_HEIGHT).build());
		}
		
		this.addDrawableChild(ButtonWidget.builder(
			Text.of("Refresh"),
			button -> {
				loadEntries();
				scrollOffset = 0;
				clearAndInit();
			}
		).dimensions(width / 2 - 110, height - 50, 100, 20).build());
		
		this.addDrawableChild(ButtonWidget.builder(
			Text.of("Close"),
			button -> close()
		).dimensions(width / 2 + 10, height - 50, 100, 20).build());
	}
	
	protected void clearAndInit() {
		clearChildren();
		init();
	}
	
	private void handleEntryClick(FileEntry entry) {
		if (entry.isDirectory) {
			currentPath = entry.path;
			scrollOffset = 0;
			loadEntries();
			clearAndInit();
		} else if (entry.isTxthFile) {
			loadTxthFile(entry.path);
		}
	}
	
	private void loadTxthFile(Path filePath) {
		try {
			String relativePath = SignScribeFileManager.getInstance().getConfigDir()
				.relativize(filePath)
				.toString()
				.replace("\\", "/");
			
			System.out.println("[SignScribe] Loading file: " + relativePath);
			SignScribePlacement.getInstance().startSession(relativePath);
			
			if (client.player != null) {
				client.player.sendMessage(
					Text.of("§a[SignScribe] Loaded file: " + filePath.getFileName()),
					true
				);
			}
			
			close();
			
		} catch (IOException e) {
			errorMessage = "Error loading file: " + e.getMessage();
			System.err.println("[SignScribe] IOException: " + e.getMessage());
			e.printStackTrace();
		} catch (com.signscribe.TxthParseException e) {
			errorMessage = "Parse error: " + e.getMessage();
			System.err.println("[SignScribe] TxthParseException: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	public void close() {
		client.setScreen(parent);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context, mouseX, mouseY, delta);
		
		context.drawCenteredTextWithShadow(
			this.textRenderer,
			this.title,
			this.width / 2,
			20,
			0xFFFFFF
		);
		
		String pathText = "Path: " + currentPath.getFileName();
		if (currentPath.getParent() != null) {
			pathText = "Path: " + currentPath.getParent().getFileName() + "/" + currentPath.getFileName();
		}
		
		if (pathText.length() > 40) {
			pathText = "..." + pathText.substring(pathText.length() - 37);
		}
		
		context.drawCenteredTextWithShadow(
			this.textRenderer,
			Text.of(pathText),
			this.width / 2,
			45,
			0xAAAAAA
		);
		
		if (!errorMessage.isEmpty()) {
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				Text.of(errorMessage),
				this.width / 2,
				70,
				0xFF5555
			);
		}
		
		SignScribePlacement placement = SignScribePlacement.getInstance();
		if (placement.hasSession()) {
			String currentFile = placement.getCurrentFilename();
			if (currentFile != null && !currentFile.isEmpty()) {
				String info = String.format("Current session: %s (%d/%d signs)",
					currentFile,
					placement.getCurrentPageIndex() + 1,
					placement.getTotalSigns()
				);
				context.drawCenteredTextWithShadow(
					this.textRenderer,
					Text.of(info),
					this.width / 2,
					85,
					0x55FF55
				);
			}
		}
		
		super.render(context, mouseX, mouseY, delta);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			close();
			return true;
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
