package com.signscribe;

import net.fabricmc.api.ModInitializer;

public class SignScribeMod implements ModInitializer {
	public static final String MODID = "signscribe";
	public static final String VERSION = "1.0.0";

	@Override
	public void onInitialize() {
		SignScribeConfig.getInstance();
	}
}
