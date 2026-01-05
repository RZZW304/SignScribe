package com.signscribe.command;

import com.signscribe.SignScribePlacement;
import com.signscribe.gui.SignScribeFileScreen;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

import java.io.IOException;

public class SignScribeCommands {
	public static void register() {
		ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
			dispatcher.register(ClientCommandManager.literal("signscribe")
				.then(ClientCommandManager.literal("open")
					.executes(context -> {
						if (context.getSource().getClient() != null) {
							context.getSource().getClient().setScreen(
								new SignScribeFileScreen(null)
							);
							return 1;
						}
						return 0;
					})
				)
				.then(ClientCommandManager.literal("next")
					.executes(context -> {
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
				.then(ClientCommandManager.literal("prev")
					.executes(context -> {
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
		});
	}
}
