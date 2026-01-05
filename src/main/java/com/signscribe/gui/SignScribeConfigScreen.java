package com.signscribe.gui;

import com.signscribe.SignScribeConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.OptionListWidget;
import net.minecraft.text.Text;

public class SignScribeConfigScreen extends Screen {
	private final Screen parent;

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
		
		super.render(context, mouseX, mouseY, delta);
	}
}
