package com.signscribe;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class SignScribeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SignScribeConfig config = SignScribeConfig.getInstance();
		SignScribeFileManager fileManager = SignScribeFileManager.getInstance();
		SignScribeData data = SignScribeData.getInstance();
		
		Path configDir = FabricLoader.getInstance().getConfigDir();
		fileManager.setConfigDir(configDir);
		data.setDataFile(configDir);
		
		try {
			fileManager.initializeDirectories();
			data.load();
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize SignScribe directories or data", e);
		}
	}
}
