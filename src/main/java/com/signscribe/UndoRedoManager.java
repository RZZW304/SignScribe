package com.signscribe;

import net.minecraft.block.Blocks;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class UndoRedoManager {
	private static UndoRedoManager INSTANCE;
	private final List<SignPlacementRecord> undoStack = new LinkedList<>();
	private final List<SignPlacementRecord> redoStack = new LinkedList<>();
	private static final int MAX_UNDO_STEPS = 50;
	
	public static UndoRedoManager getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new UndoRedoManager();
		}
		return INSTANCE;
	}
	
	public void recordPlacement(ServerPlayerEntity player, World world, BlockPos pos, SignPage signPage) {
		SignPlacementRecord record = new SignPlacementRecord(
			player.getUuid(),
			pos,
			signPage,
			world.getBlockState(pos)
		);
		
		undoStack.add(0, record);
		redoStack.clear();
		
		if (undoStack.size() > MAX_UNDO_STEPS) {
			undoStack.remove(undoStack.size() - 1);
		}
	}
	
	public boolean undo(ServerPlayerEntity player, World world) {
		if (undoStack.isEmpty()) {
			return false;
		}
		
		UUID playerId = player.getUuid();
		SignPlacementRecord record = findFirstRecordForPlayer(undoStack, playerId);
		
		if (record == null) {
			return false;
		}
		
		undoStack.remove(record);
		redoStack.add(0, record);
		
		world.removeBlock(record.pos, false);
		
		player.sendMessage(Text.literal("§a[SignScribe] Undo: Removed sign at " + formatPos(record.pos)), true);
		
		return true;
	}
	
	public boolean redo(ServerPlayerEntity player, World world) {
		if (redoStack.isEmpty()) {
			return false;
		}
		
		UUID playerId = player.getUuid();
		SignPlacementRecord record = findFirstRecordForPlayer(redoStack, playerId);
		
		if (record == null) {
			return false;
		}
		
		redoStack.remove(record);
		undoStack.add(0, record);
		
		world.setBlockState(record.pos, Blocks.OAK_SIGN.getDefaultState());
		SignBlockEntity signEntity = (SignBlockEntity) world.getBlockEntity(record.pos);
		
		if (signEntity != null) {
			SignTextRenderer.applyTextToSign(signEntity, record.signPage);
		}
		
		player.sendMessage(Text.literal("§a[SignScribe] Redo: Replaced sign at " + formatPos(record.pos)), true);
		
		return true;
	}
	
	public boolean canUndo(ServerPlayerEntity player) {
		UUID playerId = player.getUuid();
		return findFirstRecordForPlayer(undoStack, playerId) != null;
	}
	
	public boolean canRedo(ServerPlayerEntity player) {
		UUID playerId = player.getUuid();
		return findFirstRecordForPlayer(redoStack, playerId) != null;
	}
	
	public void clearForPlayer(UUID playerId) {
		undoStack.removeIf(record -> record.playerId.equals(playerId));
		redoStack.removeIf(record -> record.playerId.equals(playerId));
	}
	
	private SignPlacementRecord findFirstRecordForPlayer(List<SignPlacementRecord> list, UUID playerId) {
		for (SignPlacementRecord record : list) {
			if (record.playerId.equals(playerId)) {
				return record;
			}
		}
		return null;
	}
	
	private String formatPos(BlockPos pos) {
		return String.format("[%d, %d, %d]", pos.getX(), pos.getY(), pos.getZ());
	}
	
	private static class SignPlacementRecord {
		final UUID playerId;
		final BlockPos pos;
		final SignPage signPage;
		final net.minecraft.block.BlockState previousState;
		
		SignPlacementRecord(UUID playerId, BlockPos pos, SignPage signPage, net.minecraft.block.BlockState previousState) {
			this.playerId = playerId;
			this.pos = pos;
			this.signPage = signPage;
			this.previousState = previousState;
		}
	}
}
