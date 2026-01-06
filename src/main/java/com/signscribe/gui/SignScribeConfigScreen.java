package com.signscribe.gui;

import com.signscribe.SignScribeFileManager;
import com.signscribe.SignScribePlacement;
import com.signscribe.SignScribeConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SignScribeConfigScreen extends Screen {
	private final Screen parent;
	private String loadMessage = "";
	private int loadMessageTimer = 0;
	private java.util.List<String> loadedFiles = new java.util.ArrayList<>();

	public SignScribeConfigScreen(Screen parent) {
		super(Text.of("SignScribe Configuration"));
		this.parent = parent;
		loadTxthFiles();
	}

	private void loadTxthFiles() {
		loadedFiles.clear();
		try {
			Path txthDir = SignScribeFileManager.getInstance().getTxthDirectory();
			if (Files.exists(txthDir) && Files.isDirectory(txthDir)) {
				try (Stream<Path> stream = Files.list(txthDir)) {
					stream.filter(p -> p.toString().toLowerCase().endsWith(".txth"))
						.map(Path::getFileName)
						.map(Path::toString)
						.sorted()
						.forEach(loadedFiles::add);
				}
			}
		} catch (IOException e) {
			System.err.println("[SignScribe] Error loading txth files: " + e.getMessage());
		}
	}

	@Override
	protected void init() {
		super.init();

		int y = 40;
		int buttonWidth = 200;
		int x = (this.width - buttonWidth) / 2;

		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Enabled: " + SignScribeConfig.getInstance().enabled),
			button -> {
				SignScribeConfig.getInstance().enabled = !SignScribeConfig.getInstance().enabled;
				button.setMessage(Text.literal("Enabled: " + SignScribeConfig.getInstance().enabled));
				SignScribeConfig.save();
			}
		).dimensions(x, y, buttonWidth, 20).build());

		y += 30;
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Auto-Advance: " + SignScribeConfig.getInstance().autoAdvance),
			button -> {
				SignScribeConfig.getInstance().autoAdvance = !SignScribeConfig.getInstance().autoAdvance;
				button.setMessage(Text.literal("Auto-Advance: " + SignScribeConfig.getInstance().autoAdvance));
				SignScribeConfig.save();
			}
		).dimensions(x, y, buttonWidth, 20).build());

		y += 30;
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Show Preview: " + SignScribeConfig.getInstance().showPreview),
			button -> {
				SignScribeConfig.getInstance().showPreview = !SignScribeConfig.getInstance().showPreview;
				button.setMessage(Text.literal("Show Preview: " + SignScribeConfig.getInstance().showPreview));
				SignScribeConfig.save();
			}
		).dimensions(x, y, buttonWidth, 20).build());

		y += 30;
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Require Empty Hand: " + SignScribeConfig.getInstance().requireEmptyHand),
			button -> {
				SignScribeConfig.getInstance().requireEmptyHand = !SignScribeConfig.getInstance().requireEmptyHand;
				button.setMessage(Text.literal("Require Empty Hand: " + SignScribeConfig.getInstance().requireEmptyHand));
				SignScribeConfig.save();
			}
		).dimensions(x, y, buttonWidth, 20).build());

		y += 30;
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Show Success Message: " + SignScribeConfig.getInstance().showSuccessMessage),
			button -> {
				SignScribeConfig.getInstance().showSuccessMessage = !SignScribeConfig.getInstance().showSuccessMessage;
				button.setMessage(Text.literal("Show Success Message: " + SignScribeConfig.getInstance().showSuccessMessage));
				SignScribeConfig.save();
			}
		).dimensions(x, y, buttonWidth, 20).build());

		y += 40;

		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Open File Picker"),
			button -> {
				loadTxthFiles();
				client.setScreen(new SignScribeFilePickerScreen(this));
			}
		).dimensions(x, y, buttonWidth, 20).build());

		y += 30;
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Done"),
			button -> this.client.setScreen(parent)
		).dimensions(x, y, buttonWidth, 20).build());
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

		int labelY = 222;
		context.drawCenteredTextWithShadow(
			this.textRenderer,
			Text.literal("Loaded .txth files:"),
			this.width / 2,
			labelY,
			0xFFFFFF
		);

		if (!loadedFiles.isEmpty()) {
			int fileY = 242;
			for (String filename : loadedFiles) {
				context.drawCenteredTextWithShadow(
					this.textRenderer,
					Text.literal("- " + filename),
					this.width / 2,
					fileY,
					0xAAAAAA
				);
				fileY += 12;
			}
		} else {
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				Text.literal("No .txth files found in config/signscribe/txth"),
				this.width / 2,
				262,
				0x888888
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
				int fileCount = loadedFiles.size();
				int statusY = 262 + (fileCount * 12);
				if (fileCount == 0) statusY = 262;
				context.drawCenteredTextWithShadow(
					this.textRenderer,
					Text.of(info),
					this.width / 2,
					statusY + 20,
					0x55FF55
				);
			}
		}

		super.render(context, mouseX, mouseY, delta);
	}

}
