package com.signscribe.handler;

import com.signscribe.SignPage;
import com.signscribe.SignScribePlacement;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.state.property.Properties;

public class SignPlacementEventHandler {
	
	public static void register() {
		UseBlockCallback.EVENT.register((player, world, hand, hit) -> {
			if (world.isClient()) {
				return handleSignInteractionClient(player, hand, hit);
			}
			return ActionResult.PASS;
		});
	}
	
	private static ActionResult handleSignInteractionClient(net.minecraft.entity.player.PlayerEntity player, net.minecraft.util.Hand hand, BlockHitResult hit) {
		if (!SignScribePlacement.getInstance().hasSession()) {
			return ActionResult.PASS;
		}

		SignPage currentPage = SignScribePlacement.getInstance().getCurrentSignPage();
		if (currentPage == null) {
			return ActionResult.PASS;
		}

		var pos = hit.getBlockPos();
		var state = player.getWorld().getBlockState(pos);

		if (!(state.getBlock() instanceof SignBlock)) {
			return ActionResult.PASS;
		}

		if (player.getWorld().getBlockEntity(pos) instanceof SignBlockEntity signEntity) {
			updateSignText(signEntity, currentPage);
			signEntity.markDirty();
		}

		sendSignUpdatePacket(pos, currentPage);

		MinecraftClient client = MinecraftClient.getInstance();
		if (client.player != null) {
			int current = SignScribePlacement.getInstance().getCurrentPageIndex() + 1;
			int total = SignScribePlacement.getInstance().getTotalSigns();
			client.player.sendMessage(Text.of("§a[SignScribe] Sign " + current + "/" + total + " placed"), true);
		}

		try {
			SignScribePlacement.getInstance().advanceToNextPage();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ActionResult.SUCCESS;
	}

	private static void updateSignText(SignBlockEntity signEntity, SignPage page) {
		try {
			var frontText = signEntity.getFrontText();
			frontText = frontText.withMessage(0, Text.literal(page.getLine(0)));
			frontText = frontText.withMessage(1, Text.literal(page.getLine(1)));
			frontText = frontText.withMessage(2, Text.literal(page.getLine(2)));
			frontText = frontText.withMessage(3, Text.literal(page.getLine(3)));
			signEntity.setText(frontText, true);
		} catch (Exception e) {
			System.err.println("[SignScribe] Error updating sign text: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static void sendSignUpdatePacket(BlockPos pos, SignPage page) {
		UpdateSignC2SPacket packet = new UpdateSignC2SPacket(pos, true,
			page.getLine(0), page.getLine(1), page.getLine(2), page.getLine(3));
		MinecraftClient client = MinecraftClient.getInstance();
		if (client.getNetworkHandler() != null) {
			client.getNetworkHandler().sendPacket(packet);
		}
	}
}
