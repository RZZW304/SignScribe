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
		System.out.println("[SignScribe] ===== Registering SignPlacementEventHandler =====");

		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			System.out.println("[SignScribe DEBUG] UseBlockCallback fired");

			if (!world.isClient()) {
				System.out.println("[SignScribe DEBUG] Not on client, passing");
				return ActionResult.PASS;
			}

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

			System.out.println("[SignScribe DEBUG] Sign detected, sending update immediately");
			updateSign(pos, currentPage);

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

			return ActionResult.SUCCESS;
		});
	}

	private static void updateSign(BlockPos pos, SignPage page) {
		try {
			System.out.println("[SignScribe DEBUG] Creating UpdateSignC2SPacket for: " + pos);
			UpdateSignC2SPacket packet = new UpdateSignC2SPacket(pos, true,
				page.getLine(0), page.getLine(1), page.getLine(2), page.getLine(3));

			MinecraftClient client = MinecraftClient.getInstance();
			if (client.getNetworkHandler() != null) {
				client.getNetworkHandler().sendPacket(packet);
				System.out.println("[SignScribe DEBUG] Packet sent successfully");
			} else {
				System.err.println("[SignScribe ERROR] NetworkHandler is null!");
			}
		} catch (Exception e) {
			System.err.println("[SignScribe ERROR] Error creating/sending packet: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
