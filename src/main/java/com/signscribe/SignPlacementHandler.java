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
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SignPlacementHandler {
	private static SignPlacementHandler INSTANCE;
	
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
				String[] lines = currentPage.getLines();
				for (int i = 0; i < lines.length; i++) {
					signEntity.setTextOnRow(i, Text.literal(lines[i]));
				}
				signEntity.markDirty();
			}
			
			if (config.showSuccessMessage) {
				player.sendMessage(Text.literal("§a[SignScribe] Sign placed (" + (placement.getCurrentPageIndex() + 1) + "/" + placement.getTotalSigns() + ")"), true);
			}
			
			if (config.autoAdvance) {
				placement.advanceToNextPage();
			}
			
			return ActionResult.SUCCESS;
		} catch (Exception e) {
			if (config.showSuccessMessage) {
				player.sendMessage(Text.literal("§c[SignScribe] Error placing sign: " + e.getMessage()), true);
			}
			return ActionResult.FAIL;
		}
	}
	
	private boolean isSignItem(Item item) {
		return item == Items.OAK_SIGN || item == Items.SPRUCE_SIGN || item == Items.BIRCH_SIGN ||
		       item == Items.JUNGLE_SIGN || item == Items.ACACIA_SIGN || item == Items.DARK_OAK_SIGN ||
		       item == Items.CRIMSON_SIGN || item == Items.WARPED_SIGN || item == Items.MANGROVE_SIGN ||
		       item == Items.CHERRY_SIGN || item == Items.BAMBOO_SIGN;
	}
}
