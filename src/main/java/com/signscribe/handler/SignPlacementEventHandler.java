package com.signscribe.handler;

import com.signscribe.SignPage;
import com.signscribe.SignScribePlacement;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.SignBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class SignPlacementEventHandler {

	public static void register() {
		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			System.out.println("[SignScribe DEBUG] UseBlockCallback fired");
			if (world.isClient()) {
				return handleSignInteractionClient(player, hand, hit);
			}
			return ActionResult.PASS;
		});
	}

	private static ActionResult handleSignInteractionClient(net.minecraft.entity.player.PlayerEntity player, net.minecraft.util.Hand hand, BlockHitResult hit) {
		System.out.println("[SignScribe DEBUG] Handling sign interaction on client");
		if (!SignScribePlacement.getInstance().hasSession()) {
			System.out.println("[SignScribe DEBUG] No active session, passing");
			return ActionResult.PASS;
		}

		SignPage currentPage = SignScribePlacement.getInstance().getCurrentSignPage();
		if (currentPage == null) {
			System.out.println("[SignScribe DEBUG] Current page is null, passing");
			return ActionResult.PASS;
		}

		var pos = hit.getBlockPos();
		var state = player.getWorld().getBlockState(pos);

		if (!(state.getBlock() instanceof SignBlock)) {
			System.out.println("[SignScribe DEBUG] Not a sign block, passing");
			return ActionResult.PASS;
		}

		System.out.println("[SignScribe DEBUG] Sign detected, sending update packet");
		sendSignUpdatePacket(pos, currentPage);

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			int current = SignScribePlacement.getInstance().getCurrentPageIndex() + 1;
			int total = SignScribePlacement.getInstance().getTotalSigns();
			client.player.sendMessage(Text.of("§a[SignScribe] Sign " + current + "/" + total + " placed"), true);
			System.out.println("[SignScribe DEBUG] Sent message to player");
		}

		try {
			SignScribePlacement.getInstance().advanceToNextPage();
			System.out.println("[SignScribe DEBUG] Advanced to next page");
		} catch (Exception e) {
			System.err.println("[SignScribe ERROR] Error advancing page: " + e.getMessage());
			e.printStackTrace();
		}

		System.out.println("[SignScribe DEBUG] Returning SUCCESS");
		return ActionResult.SUCCESS;
	}

	private static void sendSignUpdatePacket(BlockPos pos, SignPage page) {
		System.out.println("[SignScribe DEBUG] Creating UpdateSignC2SPacket");
		try {
			UpdateSignC2SPacket packet = new UpdateSignC2SPacket(pos, true,
				page.getLine(0), page.getLine(1), page.getLine(2), page.getLine(3));
			System.out.println("[SignScribe DEBUG] Packet created: " + pos);
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.getNetworkHandler() != null) {
				client.getNetworkHandler().sendPacket(packet);
				System.out.println("[SignScribe DEBUG] Packet sent");
			} else {
				System.err.println("[SignScribe ERROR] NetworkHandler is null");
			}
		} catch (Exception e) {
			System.err.println("[SignScribe ERROR] Error creating/sending packet: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
