package com.signscribe.gui;

import com.signscribe.SignPage;
import com.signscribe.SignScribePlacement;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class SignEditScreen extends Screen {
	private static final int BUTTON_WIDTH = 100;
	private static final int BUTTON_HEIGHT = 20;
	
	private final Screen parent;
	private final SignPage originalPage;
	private TextFieldWidget[] lineFields;
	
	public SignEditScreen(Screen parent, SignPage page) {
		super(Text.of("Edit Sign Text"));
		this.parent = parent;
		this.originalPage = page;
		this.lineFields = new TextFieldWidget[4];
	}
	
	@Override
	protected void init() {
		super.init();
		
		int centerX = width / 2;
		int startY = height / 2 - 50;
		
		for (int i = 0; i < 4; i++) {
			String initialText = originalPage != null ? originalPage.getLines()[i] : "";
			
			TextFieldWidget field = new TextFieldWidget(
				this.textRenderer,
				centerX - 100,
				startY + (i * 25),
				200,
				20,
				Text.of("Line " + (i + 1))
			);
			
			field.setText(initialText);
			field.setMaxLength(14);
			field.setPlaceholder(Text.of(String.format("Line %d (14 chars max)", i + 1)));
			
			this.lineFields[i] = field;
			this.addDrawableChild(field);
		}
		
		this.addDrawableChild(ButtonWidget.builder(
			Text.of("Save"),
			button -> saveChanges()
		).dimensions(
			centerX - BUTTON_WIDTH / 2,
			height / 2 + 60,
			BUTTON_WIDTH,
			BUTTON_HEIGHT
		).build());
		
		this.addDrawableChild(ButtonWidget.builder(
			Text.of("Cancel"),
			button -> close()
		).dimensions(
			centerX - BUTTON_WIDTH / 2,
			height / 2 + 90,
			BUTTON_WIDTH,
			BUTTON_HEIGHT
		).build());
	}
	
	private void saveChanges() {
		if (originalPage == null) {
			close();
			return;
		}
		
		String[] newLines = new String[4];
		for (int i = 0; i < 4; i++) {
			newLines[i] = lineFields[i].getText();
		}
		
		SignScribePlacement placement = SignScribePlacement.getInstance();
		int currentIndex = placement.getCurrentPageIndex();
		
		try {
			SignPage updatedPage = new SignPage(currentIndex + 1, newLines[0], newLines[1], newLines[2], newLines[3]);
			placement.goToPage(currentIndex);
			
			MinecraftClient.getInstance().player.sendMessage(Text.of("§a[SignScribe] Sign text updated"), true);
			
			close();
		} catch (Exception e) {
			MinecraftClient.getInstance().player.sendMessage(Text.of("§c[SignScribe] Error updating sign: " + e.getMessage()), true);
		}
	}
	
	@Override
	public void close() {
		client.setScreen(parent);
	}
	
	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		
		context.drawCenteredTextWithShadow(
			this.textRenderer,
			this.title,
			this.width / 2,
			height / 2 - 80,
			0xFFFFFF
		);
		
		if (originalPage != null) {
			String info = String.format("Editing sign %d/%d", 
				SignScribePlacement.getInstance().getCurrentPageIndex() + 1,
				SignScribePlacement.getInstance().getTotalSigns());
			
			context.drawCenteredTextWithShadow(
				this.textRenderer,
				Text.of(info),
				this.width / 2,
				height / 2 - 60,
				0xAAAAAA
			);
		}
		
		super.render(context, mouseX, mouseY, delta);
	}
	
	@Override
	public boolean charTyped(char chr, int modifiers) {
		for (TextFieldWidget field : lineFields) {
			field.charTyped(chr, modifiers);
		}
		return super.charTyped(chr, modifiers);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		for (TextFieldWidget field : lineFields) {
			field.keyPressed(keyCode, scanCode, modifiers);
		}
		return super.keyPressed(keyCode, scanCode, modifiers);
	}
}
