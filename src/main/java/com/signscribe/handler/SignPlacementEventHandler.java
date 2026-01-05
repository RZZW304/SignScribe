package com.signscribe.handler;

import com.signscribe.SignPage;
import com.signscribe.SignScribePlacement;
import com.signscribe.SignScribeConfig;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class SignPlacementEventHandler {

	private static boolean processingSign = false;
	private static BlockPos lastSignPos = null;
	private static SignPage lastSignPage = null;

	public static void register() {
		System.out.println("[SignScribe] ===== Registering SignPlacementEventHandler =====");

		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			System.out.println("[SignScribe DEBUG] UseBlockCallback fired");

			if (processingSign) {
				System.out.println("[SignScribe DEBUG] Already processing a sign, passing");
				return ActionResult.PASS;
			}

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

			if (SignScribeConfig.getInstance().requireEmptyHand) {
				if (!player.getStackInHand(hand).isEmpty()) {
					System.out.println("[SignScribe DEBUG] Hand not empty, passing");
					return ActionResult.PASS;
				}
			}

			System.out.println("[SignScribe DEBUG] Sign detected at " + pos + ", scheduling update");

			lastSignPos = pos;
			lastSignPage = currentPage;

			processingSign = true;
			try {
				MinecraftClient client = MinecraftClient.getInstance();
				client.execute(() -> {
					try {
						updateSignText(lastSignPos, lastSignPage);

						int current = SignScribePlacement.getInstance().getCurrentPageIndex() + 1;
						int total = SignScribePlacement.getInstance().getTotalSigns();
						
						if (client.player != null) {
							client.player.sendMessage(Text.of("§a[SignScribe] Sign " + current + "/" + total + " placed"), true);
						}

						try {
							SignScribePlacement.getInstance().advanceToNextPage();
							System.out.println("[SignScribe DEBUG] Advanced to next page");
						} catch (Exception e) {
							System.err.println("[SignScribe ERROR] Error advancing page: " + e.getMessage());
							e.printStackTrace();
						}
					} catch (Exception e) {
						System.err.println("[SignScribe ERROR] Error in delayed update: " + e.getMessage());
						e.printStackTrace();
					} finally {
						processingSign = false;
					}
				});

				return ActionResult.PASS;
			} catch (Exception e) {
				System.err.println("[SignScribe ERROR] Error processing sign: " + e.getMessage());
				e.printStackTrace();
				processingSign = false;
				return ActionResult.PASS;
			}
		});
	}

	private static void updateSignText(BlockPos pos, SignPage page) {
		try {
			System.out.println("[SignScribe DEBUG] Updating sign at: " + pos);
			
			MinecraftClient client = MinecraftClient.getInstance();
			if (client.world == null) {
				System.err.println("[SignScribe ERROR] World is null!");
				return;
			}

			var blockEntity = client.world.getBlockEntity(pos);
			if (!(blockEntity instanceof SignBlockEntity signEntity)) {
				System.err.println("[SignScribe ERROR] Block entity is not a SignBlockEntity!");
				return;
			}

			System.out.println("[SignScribe DEBUG] SignBlockEntity found, attempting to set text");

			try {
				var frontText = signEntity.getFrontText();
				var updatedText = frontText.withMessage(0, Text.literal(page.getLine(0)))
					.withMessage(1, Text.literal(page.getLine(1)))
					.withMessage(2, Text.literal(page.getLine(2)))
					.withMessage(3, Text.literal(page.getLine(3)));
				
				java.lang.reflect.Field frontTextField = SignBlockEntity.class.getDeclaredField("frontText");
				frontTextField.setAccessible(true);
				frontTextField.set(signEntity, updatedText);
				
				signEntity.markDirty();
				client.world.updateListeners(pos, signEntity.getCachedState(), signEntity.getCachedState(), 3);
				
				System.out.println("[SignScribe DEBUG] Sign text updated successfully via reflection");
				return;
			} catch (Exception e1) {
				System.err.println("[SignScribe ERROR] Reflection approach failed: " + e1.getMessage());
			}

			try {
				net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket packet = 
					new net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket(
						pos, true, 
						page.getLine(0), page.getLine(1), page.getLine(2), page.getLine(3)
					);
				
				if (client.getNetworkHandler() != null) {
					client.getNetworkHandler().sendPacket(packet);
					System.out.println("[SignScribe DEBUG] Packet sent successfully");
				} else {
					System.err.println("[SignScribe ERROR] NetworkHandler is null!");
				}
			} catch (Exception e2) {
				System.err.println("[SignScribe ERROR] Packet approach failed: " + e2.getMessage());
				throw e2;
			}

		} catch (Exception e) {
			System.err.println("[SignScribe ERROR] Error updating sign text: " + e.getMessage());
			e.printStackTrace();
			throw e;
		}
	}
}
