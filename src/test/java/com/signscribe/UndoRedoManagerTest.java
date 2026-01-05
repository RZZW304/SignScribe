package com.signscribe;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UndoRedoManagerTest {
	
	@TempDir
	Path tempDir;
	
	private UndoRedoManager manager;
	private UUID player1Id;
	private UUID player2Id;
	
	@BeforeEach
	void setUp() {
		manager = UndoRedoManager.getInstance();
		player1Id = UUID.randomUUID();
		player2Id = UUID.randomUUID();
	}
	
	@Test
	void testRecordPlacement() {
		SignPage page = new SignPage(1, "Line 1", "Line 2", "Line 3", "Line 4");
		
		UndoRedoManager.getInstance().clearForPlayer(player1Id);
		UndoRedoManager.getInstance().recordPlacement(
			Mockito.mock(ServerPlayerEntity.class),
			Mockito.mock(net.minecraft.world.World.class),
			Mockito.mock(net.minecraft.util.math.BlockPos.class),
			page
		);
		
		assertTrue(UndoRedoManager.getInstance().canUndo(Mockito.mock(ServerPlayerEntity.class)));
	}
	
	@Test
	void testCanUndoFalseInitially() {
		UndoRedoManager.getInstance().clearForPlayer(player1Id);
		
		ServerPlayerEntity player = mockPlayer(player1Id);
		assertFalse(UndoRedoManager.getInstance().canUndo(player));
	}
	
	@Test
	void testCanRedoFalseInitially() {
		UndoRedoManager.getInstance().clearForPlayer(player1Id);
		
		ServerPlayerEntity player = mockPlayer(player1Id);
		assertFalse(UndoRedoManager.getInstance().canRedo(player));
	}
	
	@Test
	void testPerPlayerIsolation() {
		SignPage page1 = new SignPage(1, "P1", "", "", "");
		SignPage page2 = new SignPage(2, "P2", "", "", "");
		
		UndoRedoManager.getInstance().clearForPlayer(player1Id);
		UndoRedoManager.getInstance().clearForPlayer(player2Id);
		
		UndoRedoManager.getInstance().recordPlacement(
			mockPlayer(player1Id),
			Mockito.mock(net.minecraft.world.World.class),
			Mockito.mock(net.minecraft.util.math.BlockPos.class),
			page1
		);
		
		ServerPlayerEntity player1 = mockPlayer(player1Id);
		ServerPlayerEntity player2 = mockPlayer(player2Id);
		
		assertTrue(UndoRedoManager.getInstance().canUndo(player1));
		assertFalse(UndoRedoManager.getInstance().canUndo(player2));
	}
	
	@Test
	void testClearForPlayer() {
		SignPage page = new SignPage(1, "Line 1", "Line 2", "Line 3", "Line 4");
		
		UndoRedoManager.getInstance().recordPlacement(
			mockPlayer(player1Id),
			Mockito.mock(net.minecraft.world.World.class),
			Mockito.mock(net.minecraft.util.math.BlockPos.class),
			page
		);
		
		UndoRedoManager.getInstance().clearForPlayer(player1Id);
		
		ServerPlayerEntity player = mockPlayer(player1Id);
		assertFalse(UndoRedoManager.getInstance().canUndo(player));
	}
	
	private ServerPlayerEntity mockPlayer(UUID uuid) {
		ServerPlayerEntity player = Mockito.mock(ServerPlayerEntity.class);
		Mockito.when(player.getUuid()).thenReturn(uuid);
		return player;
	}
}
