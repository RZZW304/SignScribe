package com.signscribe.client;

import com.signscribe.SignScribeConfig;
import com.signscribe.SignScribePlacement;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class SignScribeKeybinds {
	private static SignScribeKeybinds INSTANCE;
	
	public KeyBinding nextPageKey;
	public KeyBinding prevPageKey;
	public KeyBinding openGuiKey;
	public KeyBinding stopSessionKey;
	
	public static SignScribeKeybinds getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignScribeKeybinds();
		}
		return INSTANCE;
	}
	
	public void register() {
		nextPageKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.signscribe.next",
			GLFW.GLFW_KEY_RIGHT_BRACKET,
			"category.signscribe"
		));
		
		prevPageKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.signscribe.prev",
			GLFW.GLFW_KEY_LEFT_BRACKET,
			"category.signscribe"
		));
		
		openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.signscribe.open",
			GLFW.GLFW_KEY_P,
			"category.signscribe"
		));
		
		stopSessionKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
			"key.signscribe.stop",
			GLFW.GLFW_KEY_ESCAPE,
			"category.signscribe"
		));
		
		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player == null) {
				return;
			}
			
			handleKeybinds(client);
		});
	}
	
	private void handleKeybinds(MinecraftClient client) {
		SignScribePlacement placement = SignScribePlacement.getInstance();
		SignScribeConfig config = SignScribeConfig.getInstance();
		
		while (nextPageKey.wasPressed()) {
			if (placement.hasSession()) {
				try {
					placement.advanceToNextPage();
					showMessage(client, "§a[SignScribe] Advanced to next page");
				} catch (Exception e) {
					showMessage(client, "§c[SignScribe] Error: " + e.getMessage());
				}
			}
		}
		
		while (prevPageKey.wasPressed()) {
			if (placement.hasSession() && placement.hasPreviousPage()) {
				try {
					placement.goToPage(placement.getCurrentPageIndex() - 1);
					showMessage(client, "§a[SignScribe] Went to previous page");
				} catch (Exception e) {
					showMessage(client, "§c[SignScribe] Error: " + e.getMessage());
				}
			}
		}
		
		while (openGuiKey.wasPressed()) {
			if (client.currentScreen == null) {
				client.setScreen(new com.signscribe.gui.SignScribeFileScreen(null));
			}
		}
		
		while (stopSessionKey.wasPressed()) {
			if (placement.hasSession()) {
				try {
					placement.endSession();
					showMessage(client, "§a[SignScribe] Session stopped");
				} catch (Exception e) {
					showMessage(client, "§c[SignScribe] Error: " + e.getMessage());
				}
			}
		}
	}
	
	private void showMessage(MinecraftClient client, String message) {
		if (client.player != null) {
			client.player.sendMessage(Text.of(message), true);
		}
	}
}
