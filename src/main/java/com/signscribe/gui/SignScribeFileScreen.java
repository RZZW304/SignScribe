package com.signscribe.gui;

import com.signscribe.SignScribePlacement;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;

import java.io.IOException;
import java.util.List;

public class SignScribeFileScreen extends Screen {
	private static final int BUTTON_WIDTH = 200;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_PADDING = 5;
	
	private final Screen parent;
	private List<String> fileNames;
	private String errorMessage = "";
	
	public SignScribeFileScreen(Screen parent) {
		super(Text.of("Select .txth File"));
		this.parent = parent;
		try {
			this.fileNames = com.signscribe.SignScribeFileManager.getInstance().listTxthFiles();
		} catch (IOException e) {
			this.fileNames = List.of();
			this.errorMessage = "Error loading files: " + e.getMessage();
		}
	}
	
	@Override
	protected void init() {
		super.init();
		
		int startY = 40;
		if (fileNames.isEmpty()) {
			if (errorMessage.isEmpty()) {
				errorMessage = "No .txth files found in config directory";
			}
		} else {
			for (int i = 0; i < fileNames.size(); i++) {
				String filename = fileNames.get(i);
				int y = startY + i * (BUTTON_HEIGHT + BUTTON_PADDING);
				
				this.addDrawableChild(ButtonWidget.builder(
					Text.of(filename),
					button -> selectFile(filename)
				).dimensions(
					(width - BUTTON_WIDTH) / 2,
					y,
					BUTTON_WIDTH,
					BUTTON_HEIGHT
				).build());
			}
		}
		
		this.addDrawableChild(ButtonWidget.builder(
			Text.of("Close"),
			button -> close()
		).dimensions(
			(width - BUTTON_WIDTH) / 2,
			height - 30,
			BUTTON_WIDTH,
			BUTTON_HEIGHT
		).build());
	}
	
	private void selectFile(String filename) {
		try {
			SignScribePlacement.getInstance().startSession(filename);
			client.setScreen(parent);
		} catch (IOException e) {
			errorMessage = "Error loading file: " + e.getMessage();
		} catch (com.signscribe.TxthParseException e) {
			errorMessage = "Parse error: " + e.getMessage();
		}
	}
	
	private void close() {
		client.setScreen(parent);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackgroundTexture(context);
		
		context.drawCenteredTextWithShadow(
			this.textRenderer,
			this.title,
			this.width / 2,
			20,
			0xFFFFFF
		);
		
		if (!errorMessage.isEmpty()) {
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				Text.of(errorMessage),
				this.width / 2,
				60,
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
					40,
					0x55FF55
				);
			}
		}
		
		super.render(context, mouseX, mouseY, delta);
	}
}
