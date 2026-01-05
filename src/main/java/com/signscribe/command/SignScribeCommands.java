package com.signscribe.command;

import com.signscribe.SignScribePlacement;
import com.signscribe.SignScribeConfig;
import com.signscribe.gui.SignScribeFileScreen;
import com.signscribe.gui.SignScribePathScreen;
import com.signscribe.gui.SignScribeFilePickerScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import java.io.IOException;

public class SignScribeCommands {
	public static void register() {
		System.out.println("[SignScribe DEBUG] Registering commands");
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			System.out.println("[SignScribe DEBUG] Command callback fired");
			dispatcher.register(ClientCommandManager.literal("signscribe")
				.executes(context -> {
					context.getSource().sendFeedback(Text.of("§a[SignScribe] Available commands: on, off, open, next, prev, status, stop"));
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
				.then(ClientCommandManager.literal("load")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe load executed");
						if (context.getSource().getClient() != null) {
							try {
								SignScribeFilePickerScreen screen = new SignScribeFilePickerScreen(null);
								context.getSource().getClient().setScreen(screen);
								context.getSource().sendFeedback(Text.of("§a[SignScribe] Opening file picker..."));
								return 1;
							} catch (Exception e) {
								System.err.println("[SignScribe ERROR] Error opening screen: " + e.getMessage());
								e.printStackTrace();
								context.getSource().sendError(Text.of("§c[SignScribe] Error: " + e.getMessage()));
								return 0;
							}
						} else {
							System.err.println("[SignScribe ERROR] Client is null in load command");
							return 0;
						}
					})
				)
				.then(ClientCommandManager.literal("open")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe open executed");
						System.out.println("[SignScribe DEBUG] Client: " + context.getSource().getClient());
						if (context.getSource().getClient() != null) {
							try {
								System.out.println("[SignScribe DEBUG] Creating SignScribePathScreen");
								SignScribePathScreen screen = new SignScribePathScreen(null);
								System.out.println("[SignScribe DEBUG] Screen created: " + screen);
								context.getSource().getClient().setScreen(screen);
								System.out.println("[SignScribe DEBUG] Screen set successfully");
								context.getSource().sendFeedback(Text.of("§a[SignScribe] Opening file loader..."));
								return 1;
							} catch (Exception e) {
								System.err.println("[SignScribe ERROR] Error opening screen: " + e.getMessage());
								e.printStackTrace();
								context.getSource().sendError(Text.of("§c[SignScribe] Error: " + e.getMessage()));
								return 0;
							}
						} else {
							System.err.println("[SignScribe ERROR] Client is null in open command");
							return 0;
						}
					})
				)
				.then(ClientCommandManager.literal("next")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe next executed");
						try {
							SignScribePlacement.getInstance().advanceToNextPage();
							context.getSource().sendFeedback(Text.of("Advanced to next page"));
							return 1;
						} catch (IOException e) {
							context.getSource().sendError(Text.of("Error: " + e.getMessage()));
							return 0;
						}
					})
				)
				.then(ClientCommandManager.literal("sign")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe sign executed");
						SignScribePlacement placement = SignScribePlacement.getInstance();
						if (!placement.hasSession()) {
							context.getSource().sendError(Text.of("No active session"));
							return 0;
						}
						try {
							placement.goToPage(placement.getCurrentPageIndex());
							context.getSource().sendFeedback(Text.of("§aJumped to current sign"));
							return 1;
						} catch (IOException e) {
							context.getSource().sendError(Text.of("Error: " + e.getMessage()));
							return 0;
						}
					})
				)
				.then(ClientCommandManager.literal("prev")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe prev executed");
						SignScribePlacement placement = SignScribePlacement.getInstance();
						if (!placement.hasSession()) {
							context.getSource().sendError(Text.of("No active session"));
							return 0;
						}
						if (!placement.hasPreviousPage()) {
							context.getSource().sendError(Text.of("No previous page"));
							return 0;
						}
						try {
							placement.goToPage(placement.getCurrentPageIndex() - 1);
							context.getSource().sendFeedback(Text.of("Went to previous page"));
							return 1;
						} catch (IOException e) {
							context.getSource().sendError(Text.of("Error: " + e.getMessage()));
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
							context.getSource().sendFeedback(Text.of("No active session"));
						}
						return 1;
					})
				)
				.then(ClientCommandManager.literal("stop")
					.executes(context -> {
						System.out.println("[SignScribe DEBUG] /signscribe stop executed");
						if (!SignScribePlacement.getInstance().hasSession()) {
							context.getSource().sendError(Text.of("No active session"));
							return 0;
						}
						try {
							SignScribePlacement.getInstance().endSession();
							context.getSource().sendFeedback(Text.of("Session stopped"));
							return 1;
						} catch (IOException e) {
							context.getSource().sendError(Text.of("Error: " + e.getMessage()));
							return 0;
						}
					})
				)
			);
			System.out.println("[SignScribe DEBUG] Commands registered successfully");
		});
	}
}
