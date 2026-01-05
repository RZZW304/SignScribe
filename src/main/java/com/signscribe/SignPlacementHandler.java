package com.signscribe;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.block.SignBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SignPlacementHandler {
	private static SignPlacementHandler INSTANCE;
	private final Map<UUID, Integer> autoAdvanceTimers = new HashMap<>();
	
	public static SignPlacementHandler getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignPlacementHandler();
		}
		return INSTANCE;
	}
	
	public void register() {
		UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
			if (world.isClient || !(player instanceof ServerPlayerEntity)) {
				return ActionResult.PASS;
			}
			
			SignScribeConfig config = SignScribeConfig.getInstance();
			
			if (!config.placeSignsInstantly) {
				return ActionResult.PASS;
			}
			
			SignScribePlacement placement = SignScribePlacement.getInstance();
			if (!placement.hasSession()) {
				return ActionResult.PASS;
			}
			
			ItemStack stack = player.getStackInHand(hand);
			Item item = stack.getItem();
			
			if (!isSignItem(item)) {
				return ActionResult.PASS;
			}
			
			if (config.requireEmptyHand && !stack.isEmpty()) {
				return ActionResult.PASS;
			}
			
			BlockPos pos = hitResult.getBlockPos();
			if (!world.getBlockState(pos).isAir() && !(world.getBlockState(pos).getBlock() instanceof SignBlock)) {
				return ActionResult.PASS;
			}
			
			return placeSign((ServerPlayerEntity) player, world, pos, hitResult, hand);
		});
	}
	
	private ActionResult placeSign(ServerPlayerEntity player, World world, BlockPos pos, BlockHitResult hitResult, Hand hand) {
		SignScribePlacement placement = SignScribePlacement.getInstance();
		SignPage currentPage = placement.getCurrentSignPage();
		
		if (currentPage == null) {
			return ActionResult.PASS;
		}
		
		SignScribeConfig config = SignScribeConfig.getInstance();
		
		try {
			world.setBlockState(pos, Blocks.OAK_SIGN.getDefaultState());
			SignBlockEntity signEntity = (SignBlockEntity) world.getBlockEntity(pos);
			
			if (signEntity != null) {
				SignTextRenderer.applyTextToSign(signEntity, currentPage);
			}
			
			if (config.showSuccessMessage) {
				player.sendMessage(Text.literal("§a[SignScribe] Sign placed (" + (placement.getCurrentPageIndex() + 1) + "/" + placement.getTotalSigns() + ")"), true);
			}
			
			if (config.autoAdvance) {
				if (config.autoAdvanceDelay > 0) {
					scheduleAutoAdvance(player, world);
				} else {
					placement.advanceToNextPage();
				}
			}
			
			return ActionResult.SUCCESS;
		} catch (Exception e) {
			if (config.showSuccessMessage) {
				player.sendMessage(Text.literal("§c[SignScribe] Error placing sign: " + e.getMessage()), true);
			}
			return ActionResult.FAIL;
		}
	}
	
	private void scheduleAutoAdvance(ServerPlayerEntity player, World world) {
		if (!(world instanceof ServerWorld)) {
			return;
		}
		
		ServerWorld serverWorld = (ServerWorld) world;
		int delay = SignScribeConfig.getInstance().autoAdvanceDelay;
		
		serverWorld.getServer().getCommands().executeWithPrefix(
			serverWorld.getCommandSource(),
			"execute as " + player.getName().getString() + " run title " + player.getName().getString() + " actionbar {\"text\":\"Auto-advancing in " + delay + " ticks...\",\"color\":\"yellow\"}"
		);
		
		UUID playerId = player.getUuid();
		if (!autoAdvanceTimers.containsKey(playerId)) {
			startTimerTask(player, serverWorld, playerId, delay);
		}
	}
	
	private void startTimerTask(ServerPlayerEntity player, ServerWorld world, UUID playerId, int delay) {
		autoAdvanceTimers.put(playerId, delay);
		
		world.getServer().submit(() -> {
			try {
				Thread.sleep(delay * 50L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			autoAdvanceTimers.remove(playerId);
			SignScribePlacement placement = SignScribePlacement.getInstance();
			try {
				if (placement.hasSession()) {
					placement.advanceToNextPage();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
	
	private boolean isSignItem(Item item) {
		return item == Items.OAK_SIGN || item == Items.SPRUCE_SIGN || item == Items.BIRCH_SIGN ||
		       item == Items.JUNGLE_SIGN || item == Items.ACACIA_SIGN || item == Items.DARK_OAK_SIGN ||
		       item == Items.CRIMSON_SIGN || item == Items.WARPED_SIGN || item == Items.MANGROVE_SIGN ||
		       item == Items.CHERRY_SIGN || item == Items.BAMBOO_SIGN;
	}
}
