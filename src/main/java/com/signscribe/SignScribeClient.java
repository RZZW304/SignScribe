package com.signscribe;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class SignScribeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SignScribeConfig config = SignScribeConfig.getInstance();
		SignScribeFileManager fileManager = SignScribeFileManager.getInstance();
		fileManager.setConfigDir(FabricLoader.getInstance().getConfigDir());
		try {
			fileManager.initializeDirectories();
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize SignScribe directories", e);
		}
	}
}
