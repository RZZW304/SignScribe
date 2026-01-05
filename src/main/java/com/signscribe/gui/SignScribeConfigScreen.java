package com.signscribe.gui;

import com.signscribe.SignScribeFileManager;
import com.signscribe.SignScribePlacement;
import com.signscribe.SignScribeConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SignScribeConfigScreen extends Screen {
	private final Screen parent;
	private TextFieldWidget pathTextField;
	private String loadMessage = "";
	private int loadMessageTimer = 0;

	public SignScribeConfigScreen(Screen parent) {
		super(Text.of("SignScribe Configuration"));
		this.parent = parent;
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
		
		int textFieldWidth = 300;
		int textFieldX = (this.width - textFieldWidth) / 2;
		this.pathTextField = new TextFieldWidget(this.textRenderer, textFieldX, y, textFieldWidth, 20, Text.literal("Enter file path..."));
		this.pathTextField.setPlaceholder(Text.literal("config/signscribe/txth/filename.txth"));
		this.pathTextField.setText("");
		this.pathTextField.setMaxLength(256);
		this.addDrawableChild(this.pathTextField);
		this.setInitialFocus(this.pathTextField);
		
		y += 30;
		
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Load File"),
			button -> loadManualPath()
		).dimensions(x, y, buttonWidth, 20).build());
		
		y += 30;
		this.addDrawableChild(ButtonWidget.builder(
			Text.literal("Open File Picker"),
			button -> {
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
		
		int labelY = 202;
		context.drawCenteredTextWithShadow(
			this.textRenderer,
			Text.literal("Load file manually:"),
			this.width / 2,
			labelY,
			0xFFFFFF
		);

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
					330,
					0x55FF55
				);
			}
		}
		
		if (loadMessageTimer > 0) {
			Formatting color = loadMessage.startsWith("✓") ? Formatting.GREEN : Formatting.RED;
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				Text.literal(loadMessage).formatted(color),
				this.width / 2,
				315,
				color.getColorValue()
			);
			loadMessageTimer--;
		}
		
		super.render(context, mouseX, mouseY, delta);
	}
	
	private void loadManualPath() {
		String inputPath = pathTextField.getText().trim();
		if (inputPath.isEmpty()) {
			setLoadMessage("✗ Please enter a file path", false);
			return;
		}
		
		try {
			Path filePath;
			if (inputPath.startsWith("/") || inputPath.contains(":/")) {
				filePath = Paths.get(inputPath);
			} else {
				filePath = SignScribeFileManager.getInstance().getConfigDir().resolve(inputPath);
			}
			
			if (!Files.exists(filePath)) {
				setLoadMessage("✗ File not found: " + filePath.getFileName(), false);
				return;
			}
			
			if (!Files.isRegularFile(filePath)) {
				setLoadMessage("✗ Not a file: " + filePath.getFileName(), false);
				return;
			}
			
			if (!inputPath.toLowerCase().endsWith(".txth") && !filePath.toString().toLowerCase().endsWith(".txth")) {
				setLoadMessage("✗ File must be .txth format", false);
				return;
			}
			
			String relativePath = SignScribeFileManager.getInstance().getConfigDir()
				.relativize(filePath)
				.toString()
				.replace("\\", "/");
			
			System.out.println("[SignScribe] Loading file from manual path: " + relativePath);
			SignScribePlacement.getInstance().startSession(relativePath);
			
			if (client.player != null) {
				client.player.sendMessage(
					Text.of("§a[SignScribe] Loaded file: " + filePath.getFileName()),
					true
				);
			}
			
			setLoadMessage("✓ Successfully loaded: " + filePath.getFileName(), true);
			pathTextField.setText("");
			
		} catch (IOException e) {
			setLoadMessage("✗ Error loading file: " + e.getMessage(), false);
			System.err.println("[SignScribe] IOException: " + e.getMessage());
			e.printStackTrace();
		} catch (com.signscribe.TxthParseException e) {
			setLoadMessage("✗ Parse error: " + e.getMessage(), false);
			System.err.println("[SignScribe] TxthParseException: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			setLoadMessage("✗ Unexpected error: " + e.getMessage(), false);
			System.err.println("[SignScribe] Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private void setLoadMessage(String message, boolean success) {
		this.loadMessage = message;
		this.loadMessageTimer = 120;
	}
	
	@Override
	public boolean charTyped(char chr, int modifiers) {
		return this.pathTextField.charTyped(chr, modifiers) || super.charTyped(chr, modifiers);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		if (keyCode == 257 || keyCode == 335) {
			if (this.pathTextField.isActive()) {
				loadManualPath();
				return true;
			}
		}
		return this.pathTextField.keyPressed(keyCode, scanCode, modifiers) || super.keyPressed(keyCode, scanCode, modifiers);
	}
}
