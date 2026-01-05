package com.signscribe;

import com.signscribe.command.SignScribeCommands;
import com.signscribe.handler.SignPlacementEventHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import java.nio.file.Path;

public class SignScribeClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		SignScribeConfig config = SignScribeConfig.getInstance();
		SignScribeFileManager fileManager = SignScribeFileManager.getInstance();
		SignScribeData data = SignScribeData.getInstance();
		
		Path configDir = FabricLoader.getInstance().getConfigDir();
		fileManager.setConfigDir(configDir);
		data.setDataFile(configDir);
		
		SignTemplateManager.getInstance().setTemplatesDirectory(configDir);
		SessionTemplateManager.getInstance().setTemplatesDirectory(configDir);
		SessionHistoryManager.getInstance().setHistoryFile(configDir);
		
		try {
			fileManager.initializeDirectories();
			data.load();
			SignTemplateManager.getInstance().loadTemplates();
			SessionTemplateManager.getInstance().loadTemplates();
			SessionHistoryManager.getInstance().loadHistory();
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize SignScribe directories or data", e);
		}
		
		SignScribeCommands.register();
		SignPlacementEventHandler.register();
	}
}
