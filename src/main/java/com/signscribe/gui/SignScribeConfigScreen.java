package com.signscribe.gui;

import com.signscribe.SignScribeConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CheckboxWidget;
import net.minecraft.text.Text;

public class SignScribeConfigScreen extends Screen {
	private static final int BUTTON_WIDTH = 150;
	private static final int BUTTON_HEIGHT = 20;
	private static final int BUTTON_PADDING = 10;
	
	private final Screen parent;
	private final SignScribeConfig config;
	
	private CheckboxWidget autoAdvanceCheckbox;
	private CheckboxWidget showPreviewCheckbox;
	private CheckboxWidget requireEmptyHandCheckbox;
	private CheckboxWidget placeSignsInstantlyCheckbox;
	private CheckboxWidget showSuccessMessageCheckbox;
	
	public SignScribeConfigScreen(Screen parent) {
		super(Text.of("SignScribe Configuration"));
		this.parent = parent;
		this.config = SignScribeConfig.getInstance();
	}
	
	@Override
	protected void init() {
		super.init();
		
		int centerX = width / 2;
		int startY = 40;
		
		autoAdvanceCheckbox = new CheckboxWidget(
			centerX - BUTTON_WIDTH / 2, startY, BUTTON_WIDTH, BUTTON_HEIGHT,
			Text.of("Auto-Advance to Next Sign"),
			config.autoAdvance
		);
		addDrawableChild(autoAdvanceCheckbox);
		
		showPreviewCheckbox = new CheckboxWidget(
			centerX - BUTTON_WIDTH / 2, startY + 30, BUTTON_WIDTH, BUTTON_HEIGHT,
			Text.of("Show Text Preview"),
			config.showPreview
		);
		addDrawableChild(showPreviewCheckbox);
		
		requireEmptyHandCheckbox = new CheckboxWidget(
			centerX - BUTTON_WIDTH / 2, startY + 60, BUTTON_WIDTH, BUTTON_HEIGHT,
			Text.of("Require Empty Hand to Place"),
			config.requireEmptyHand
		);
		addDrawableChild(requireEmptyHandCheckbox);
		
		placeSignsInstantlyCheckbox = new CheckboxWidget(
			centerX - BUTTON_WIDTH / 2, startY + 90, BUTTON_WIDTH, BUTTON_HEIGHT,
			Text.of("Place Signs Instantly"),
			config.placeSignsInstantly
		);
		addDrawableChild(placeSignsInstantlyCheckbox);
		
		showSuccessMessageCheckbox = new CheckboxWidget(
			centerX - BUTTON_WIDTH / 2, startY + 120, BUTTON_WIDTH, BUTTON_HEIGHT,
			Text.of("Show Success Messages"),
			config.showSuccessMessage
		);
		addDrawableChild(showSuccessMessageCheckbox);
		
		this.addDrawableChild(ButtonWidget.builder(
			Text.of("Save & Close"),
			button -> saveAndClose()
		).dimensions(
			centerX - BUTTON_WIDTH / 2,
			height - 60,
			BUTTON_WIDTH,
			BUTTON_HEIGHT
		).build());
		
		this.addDrawableChild(ButtonWidget.builder(
			Text.of("Cancel"),
			button -> close()
		).dimensions(
			centerX - BUTTON_WIDTH / 2,
			height - 35,
			BUTTON_WIDTH,
			BUTTON_HEIGHT
		).build());
	}
	
	private void saveAndClose() {
		config.autoAdvance = autoAdvanceCheckbox.isChecked();
		config.showPreview = showPreviewCheckbox.isChecked();
		config.requireEmptyHand = requireEmptyHandCheckbox.isChecked();
		config.placeSignsInstantly = placeSignsInstantlyCheckbox.isChecked();
		config.showSuccessMessage = showSuccessMessageCheckbox.isChecked();
		
		try {
			SignScribeConfig.save();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		close();
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
			20,
			0xFFFFFF
		);
		
		context.drawTextWithShadow(
			this.textRenderer,
			Text.of("§7Auto-Advance Delay: " + config.autoAdvanceDelay + " ticks"),
			width / 2 - BUTTON_WIDTH / 2,
			155,
			0xAAAAAA
		);
		
		super.render(context, mouseX, mouseY, delta);
	}
}
