package com.signscribe;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class SignScribeConfig {
	private static SignScribeConfig INSTANCE;

	public boolean enabled = true;
	public boolean autoAdvance = true;
	public int autoAdvanceDelay = 20;
	public boolean showPreview = true;
	public boolean requireEmptyHand = true;
	public boolean placeSignsInstantly = false;
	public boolean showSuccessMessage = true;

	public static SignScribeConfig getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignScribeConfig();
		}
		return INSTANCE;
	}

	public static void load() {
	}

	public static void save() {
	}

	@Environment(EnvType.CLIENT)
	public Screen getConfigScreen(Screen parent) {
		return new com.signscribe.gui.SignScribeFilePickerScreen(parent);
	}
}
