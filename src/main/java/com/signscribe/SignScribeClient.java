package com.signscribe;

import com.signscribe.client.SignPreviewRenderer;
import com.signscribe.client.SignScribeKeybinds;
import com.signscribe.command.SignScribeCommands;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
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
		SignScribeConfig.setConfigPath(configDir);
		
		try {
			fileManager.initializeDirectories();
			data.load();
			SignScribeConfig.load();
		} catch (Exception e) {
			throw new RuntimeException("Failed to initialize SignScribe directories or data", e);
		}
		
		SignScribeCommands.register();
		SignPreviewRenderer.getInstance().register();
		SignScribeKeybinds.getInstance().register();
		
		HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
			SignPreviewRenderer.getInstance().render(drawContext, tickDelta);
		});
	}
}
