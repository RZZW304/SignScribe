package com.signscribe.handler;

import com.signscribe.SignPage;
import com.signscribe.SignScribePlacement;
import com.signscribe.SignScribeConfig;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientBlockEntityEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;

public class SignPlacementEventHandler {

	private static final ThreadLocal<SignBlockEntity> lastSignEntity = new ThreadLocal<>();
	private static MinecraftClient client = null;

	public static void register() {
		System.out.println("[SignScribe] ===== Registering SignPlacementEventHandler =====");

		registerUseBlockCallback();
		registerBlockEntityCallback();

		System.out.println("[SignScribe] ===== Handler registration complete =====");
	}

	public static void setClient(MinecraftClient client) {
		System.out.println("[SignScribe DEBUG] Client set to: " + client);
		SignPlacementEventHandler.client = client;
	}

	private static void registerUseBlockCallback() {
		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			System.out.println("[SignScribe DEBUG] UseBlockCallback fired");

			if (!world.isClient()) {
				System.out.println("[SignScribe DEBUG] Not on client, passing");
				return ActionResult.PASS;
			}

			if (!SignScribeConfig.getInstance().enabled) {
				System.out.println("[SignScribe DEBUG] Mod disabled, passing");
				return ActionResult.PASS;
			}

			if (!SignScribePlacement.getInstance().hasSession()) {
				System.out.println("[SignScribe DEBUG] No active session, passing");
				return ActionResult.PASS;
			}

			if (SignScribeConfig.getInstance().requireEmptyHand) {
				if (!player.getStackInHand(hand).isEmpty()) {
					System.out.println("[SignScribe DEBUG] Hand not empty, passing");
					return ActionResult.PASS;
				}
			}

			var pos = hit.getBlockPos();
			var state = player.getWorld().getBlockState(pos);

			System.out.println("[SignScribe DEBUG] Block at " + pos + " is " + state.getBlock());

			if (state.getBlock() instanceof SignBlock) {
				System.out.println("[SignScribe DEBUG] Existing sign detected, applying text");
				applySignText(pos);
			}

			return ActionResult.PASS;
		});
	}

	private static void registerBlockEntityCallback() {
		ClientBlockEntityEvents.BLOCK_ENTITY_LOAD.register((blockEntity, world) -> {
			if (!SignScribeConfig.getInstance().enabled) {
				return;
			}

			if (!SignScribePlacement.getInstance().hasSession()) {
				return;
			}

			if (blockEntity instanceof SignBlockEntity signEntity) {
				System.out.println("[SignScribe DEBUG] SignBlockEntity loaded at: " + blockEntity.getPos());
				lastSignEntity.set(signEntity);
				applySignText(blockEntity.getPos());
			}
		});
	}

	private static void applySignText(net.minecraft.util.math.BlockPos pos) {
		try {
			SignPage currentPage = SignScribePlacement.getInstance().getCurrentSignPage();

			if (currentPage == null) {
				System.out.println("[SignScribe DEBUG] Current page is null, not applying text");
				return;
			}

			System.out.println("[SignScribe DEBUG] Applying page to sign at: " + pos);

			MinecraftClient mc = client != null ? client : MinecraftClient.getInstance();
			
			if (mc == null || mc.getNetworkHandler() == null) {
				System.err.println("[SignScribe ERROR] Client or NetworkHandler is null!");
				return;
			}

			mc.execute(() -> {
				try {
					net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket packet =
						new net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket(
							pos,
							true,
							currentPage.getLine(0),
							currentPage.getLine(1),
							currentPage.getLine(2),
							currentPage.getLine(3)
						);

					mc.getNetworkHandler().sendPacket(packet);
					System.out.println("[SignScribe DEBUG] Sign update packet sent");

					SignScribePlacement.getInstance().advanceToNextPage();
					System.out.println("[SignScribe DEBUG] Advanced to next page");

					int current = SignScribePlacement.getInstance().getCurrentPageIndex() + 1;
					int total = SignScribePlacement.getInstance().getTotalSigns();

					if (mc.player != null) {
						mc.player.sendMessage(Text.of("§a[SignScribe] Sign " + current + "/" + total + " placed"), true);
						System.out.println("[SignScribe DEBUG] Sent chat message: Sign " + current + "/" + total + " placed");
					}
				} catch (Exception e) {
					System.err.println("[SignScribe ERROR] Error in applySignText: " + e.getMessage());
					e.printStackTrace();
				}
			});
		} catch (Exception e) {
			System.err.println("[SignScribe ERROR] Error applying sign text: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
