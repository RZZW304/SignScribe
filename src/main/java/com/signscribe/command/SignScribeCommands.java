package com.signscribe.command;

import com.signscribe.SignScribePlacement;
import com.signscribe.SignScribeConfig;
import com.signscribe.gui.SignScribeFilePickerScreen;
import com.signscribe.SignScribeFileManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class SignScribeCommands {
	public static void register() {
		System.out.println("[SignScribe DEBUG] Registering commands");
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			System.out.println("[SignScribe DEBUG] Command callback fired");
			dispatcher.register(ClientCommandManager.literal("signscribe")
				.executes(context -> {
					context.getSource().sendFeedback(Text.of("§a[SignScribe] Available commands: on, off, load, next, prev, status, stop, help"));
					return 1;
				})
				.then(ClientCommandManager.literal("on")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe on executed");
						SignScribeConfig.getInstance().enabled = true;
						context.getSource().sendFeedback(Text.of("§a[SignScribe] Mod enabled"));
						return 1;
					})
				)
				.then(ClientCommandManager.literal("off")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe off executed");
						SignScribeConfig.getInstance().enabled = false;
						context.getSource().sendFeedback(Text.of("§c[SignScribe] Mod disabled"));
						return 1;
					})
				)
				.then(ClientCommandManager.literal("help")
					.executes(context -> {
						context.getSource().sendFeedback(Text.of("§aSignScribe Commands:"));
						context.getSource().sendFeedback(Text.of("§e/signscribe§r - Show available commands"));
						context.getSource().sendFeedback(Text.of("§e/signscribe on§r - Enable the mod"));
						context.getSource().sendFeedback(Text.of("§e/signscribe off§r - Disable the mod"));
						context.getSource().sendFeedback(Text.of("§e/signscribe load§r - Load all .txth files from config/signscribe/txth and open file picker"));
						context.getSource().sendFeedback(Text.of("§e/signscribe next§r - Advance to the next sign in the loaded file"));
						context.getSource().sendFeedback(Text.of("§e/signscribe prev§r - Go to the previous sign"));
						context.getSource().sendFeedback(Text.of("§e/signscribe status§r - Display current session information including file name and progress"));
						context.getSource().sendFeedback(Text.of("§e/signscribe stop§r - End the current placement session"));
						context.getSource().sendFeedback(Text.of("§e/signscribe sign§r - Jump to the current sign position"));
						context.getSource().sendFeedback(Text.of("§e/signscribe help§r - Show this help message"));
						return 1;
					})
				)
				.then(ClientCommandManager.literal("load")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe load executed");
						if (context.getSource().getClient() != null) {
							try {
								Path txthDir = SignScribeFileManager.getInstance().getTxthDirectory();
								if (!Files.exists(txthDir)) {
									context.getSource().sendError(Text.of("§c[SignScribe] txth directory does not exist: " + txthDir));
									return 0;
								}

								try (Stream<Path> stream = Files.list(txthDir)) {
									long fileCount = stream.filter(p -> p.toString().toLowerCase().endsWith(".txth")).count();
									context.getSource().sendFeedback(Text.of("§a[SignScribe] Loaded " + fileCount + " .txth files from config/signscribe/txth"));
								}

								SignScribeFilePickerScreen screen = new SignScribeFilePickerScreen(null);
								context.getSource().getClient().setScreen(screen);
								return 1;
							} catch (IOException e) {
								System.err.println("[SignScribe ERROR] Error loading files: " + e.getMessage());
								e.printStackTrace();
								context.getSource().sendError(Text.of("§c[SignScribe] Error loading files: " + e.getMessage()));
								return 0;
							}
						} else {
							System.err.println("[SignScribe ERROR] Client is null in load command");
							return 0;
						}
					})
				)
				.then(ClientCommandManager.literal("next")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe next executed");
						try {
							SignScribePlacement.getInstance().advanceToNextPage();
							context.getSource().sendFeedback(Text.of("§aAdvanced to next page"));
							return 1;
						} catch (IOException e) {
							context.getSource().sendError(Text.of("§cError: " + e.getMessage()));
							return 0;
						}
					})
				)
				.then(ClientCommandManager.literal("prev")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe prev executed");
						SignScribePlacement placement = SignScribePlacement.getInstance();
						if (!placement.hasSession()) {
							context.getSource().sendError(Text.of("§cNo active session"));
							return 0;
						}
						if (!placement.hasPreviousPage()) {
							context.getSource().sendError(Text.of("§cNo previous page"));
							return 0;
						}
						try {
							placement.goToPage(placement.getCurrentPageIndex() - 1);
							context.getSource().sendFeedback(Text.of("§aWent to previous page"));
							return 1;
						} catch (IOException e) {
							context.getSource().sendError(Text.of("§cError: " + e.getMessage()));
							return 0;
						}
					})
				)
				.then(ClientCommandManager.literal("status")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe status executed");
						SignScribePlacement placement = SignScribePlacement.getInstance();
						if (placement.hasSession()) {
							String status = String.format(
								"Session: %s (%d/%d signs)",
								placement.getCurrentFilename(),
								placement.getCurrentPageIndex() + 1,
								placement.getTotalSigns()
							);
							context.getSource().sendFeedback(Text.of(status));
						} else {
							context.getSource().sendFeedback(Text.of("§cNo active session"));
						}
						return 1;
					})
				)
				.then(ClientCommandManager.literal("stop")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe stop executed");
						if (!SignScribePlacement.getInstance().hasSession()) {
							context.getSource().sendError(Text.of("§cNo active session"));
							return 0;
						}
						try {
							SignScribePlacement.getInstance().endSession();
							context.getSource().sendFeedback(Text.of("§aSession stopped"));
							return 1;
						} catch (IOException e) {
							context.getSource().sendError(Text.of("§cError: " + e.getMessage()));
							return 0;
						}
					})
				)
				.then(ClientCommandManager.literal("sign")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe sign executed");
						SignScribePlacement placement = SignScribePlacement.getInstance();
						if (!placement.hasSession()) {
							context.getSource().sendError(Text.of("§cNo active session"));
							return 0;
						}
						try {
							placement.goToPage(placement.getCurrentPageIndex());
							context.getSource().sendFeedback(Text.of("§aJumped to current sign"));
							return 1;
						} catch (IOException e) {
							context.getSource().sendError(Text.of("§cError: " + e.getMessage()));
							return 0;
						}
					})
				)
			);
			System.out.println("[SignScribe DEBUG] Commands registered successfully");
		});
	}
}
