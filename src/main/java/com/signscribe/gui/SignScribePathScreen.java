package com.signscribe.gui;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

public class SignScribePathScreen extends Screen {
	private final Screen parent;
	private TextFieldWidget pathField;
	private String errorMessage = "";

	public SignScribePathScreen(Screen parent) {
		super(Text.of("Load .txth File"));
		this.parent = parent;
	}

	@Override
	protected void init() {
		super.init();

		int fieldWidth = 300;
		int fieldX = (this.width - fieldWidth) / 2;
		int fieldY = 60;

		pathField = new TextFieldWidget(this.textRenderer, fieldX, fieldY, fieldWidth, 20, Text.of("Path to .txth file"));
		pathField.setMaxLength(256);
		pathField.setPlaceholder(Text.literal("e.g., config/signscribe/txth/story.txth or story.txth"));
		pathField.setText("");
		addSelectableChild(pathField);
		setFocused(pathField);

		int buttonWidth = 150;
		int buttonX = (this.width - buttonWidth) / 2;

		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Load File"),
			button -> loadFile()
		).dimensions(buttonX, fieldY + 40, buttonWidth, 20).build());

		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Browse Files"),
			button -> browseFiles()
		).dimensions(buttonX, fieldY + 70, buttonWidth, 20).build());

		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Cancel"),
			button -> close()
		).dimensions(buttonX, fieldY + 100, buttonWidth, 20).build());
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

		context.drawCenteredTextWithShadow(
			this.textRenderer,
			Text.literal("Enter file path relative to config directory:"),
			this.width / 2,
			45,
			0xAAAAAA
		);

		if (!errorMessage.isEmpty()) {
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				Text.literal(errorMessage),
				this.width / 2,
				140,
				0xFF5555
			);
		}

		context.drawCenteredTextWithShadow(
			this.textRenderer,
			Text.literal("Examples:"),
			this.width / 2,
			165,
			0xAAAAAA
		);

		context.drawCenteredTextWithShadow(
			this.textRenderer,
			Text.literal("• story.txth"),
			this.width / 2,
			180,
			0x888888
		);

		context.drawCenteredTextWithShadow(
			this.textRenderer,
			Text.literal("• config/signscribe/txth/adventure.txth"),
			this.width / 2,
			195,
			0x888888
		);

		this.pathField.render(context, mouseX, mouseY, delta);

		super.render(context, mouseX, mouseY, delta);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 256) {
			close();
			return true;
		}
		return this.pathField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public void close() {
		client.setScreen(parent);
	}

	private void loadFile() {
		String path = pathField.getText().trim();
		if (path.isEmpty()) {
			errorMessage = "Please enter a file path";
			return;
		}

		try {
			com.signscribe.SignScribePlacement.getInstance().startSession(path);
			if (client.player != null) {
				client.player.sendMessage(
					Text.of("§a[SignScribe] Loaded file: " + path),
					true
				);
			}
			close();
		} catch (java.io.IOException e) {
			errorMessage = "Error loading file: " + e.getMessage();
			System.err.println("[SignScribe] IOException: " + e.getMessage());
			e.printStackTrace();
		} catch (com.signscribe.TxthParseException e) {
			errorMessage = "Parse error: " + e.getMessage();
			System.err.println("[SignScribe] TxthParseException: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void browseFiles() {
		client.setScreen(new SignScribeFileScreen(this));
	}
}
