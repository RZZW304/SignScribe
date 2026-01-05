package com.signscribe.handler;

import com.signscribe.SignPage;
import com.signscribe.SignScribePlacement;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.UpdateSignC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

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
		
		BlockEntity be = player.getWorld().getBlockEntity(pos);
		if (!(be instanceof SignBlockEntity signEntity)) {
			return ActionResult.PASS;
		}
		
		applyTextToSign(signEntity, currentPage);
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
	
	private static void applyTextToSign(SignBlockEntity signEntity, SignPage page) {
		for (int i = 0; i < 4; i++) {
			Text lineText = Text.literal(page.getLine(i));
			try {
				signEntity.getClass().getMethod("setText", int.class, Text.class).invoke(signEntity, i, lineText);
			} catch (Exception e) {
				try {
					java.lang.reflect.Field field = signEntity.getClass().getDeclaredField("messages");
					field.setAccessible(true);
					Text[] messages = (Text[]) field.get(signEntity);
					messages[i] = lineText;
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		signEntity.markDirty();
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
