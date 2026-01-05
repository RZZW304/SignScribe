package com.signscribe.client;

import com.signscribe.SignPage;
import com.signscribe.SignScribeConfig;
import com.signscribe.SignScribePlacement;
import net.fabricmc.api.ClientTickEvent;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class SignPreviewRenderer {
	private static SignPreviewRenderer INSTANCE;
	private boolean showPreview = false;
	
	public static SignPreviewRenderer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignPreviewRenderer();
		}
		return INSTANCE;
	}
	
	public void register() {
		ClientTickEvent.END_CLIENT_TICK.register(client -> {
			if (client.player == null) {
				showPreview = false;
				return;
			}
			
			SignScribeConfig config = SignScribeConfig.getInstance();
			if (!config.showPreview) {
				showPreview = false;
				return;
			}
			
			SignScribePlacement placement = SignScribePlacement.getInstance();
			if (!placement.hasSession()) {
				showPreview = false;
				return;
			}
			
			Item heldItem = client.player.getMainHandStack().getItem();
			showPreview = isSignItem(heldItem);
		});
	}
	
	public void render(DrawContext context, float tickDelta) {
		if (!showPreview) {
			return;
		}
		
		SignScribePlacement placement = SignScribePlacement.getInstance();
		SignPage currentPage = placement.getCurrentSignPage();
		
		if (currentPage == null) {
			return;
		}
		
		MinecraftClient client = MinecraftClient.getInstance();
		InGameHud hud = client.inGameHud;
		if (hud == null) {
			return;
		}
		
		int x = 10;
		int y = client.getWindow().getScaledHeight() / 2 - 40;
		
		drawPreviewBox(context, x, y, currentPage, placement);
	}
	
	private void drawPreviewBox(DrawContext context, int x, int y, SignPage page, SignScribePlacement placement) {
		int boxWidth = 150;
		int boxHeight = 100;
		int lineHeight = 18;
		
		context.fill(x, y, x + boxWidth, y + boxHeight, 0x80000000);
		context.fill(x, y, x + boxWidth, y + 2, 0xFFFFFFFF);
		
		String title = String.format("SignScribe Preview (%d/%d)", 
			placement.getCurrentPageIndex() + 1, 
			placement.getTotalSigns());
		
		int textY = y + 10;
		context.drawText(MinecraftClient.getInstance().textRenderer, 
			Text.of(title), x + 5, textY, 0xFFFFFF, true);
		
		textY += 20;
		String[] lines = page.getLines();
		for (int i = 0; i < lines.length; i++) {
			context.drawText(MinecraftClient.getInstance().textRenderer, 
				Text.of(lines[i]), x + 5, textY + (i * lineHeight), 0xAAAAFF, true);
		}
	}
	
	private boolean isSignItem(Item item) {
		return item == Items.OAK_SIGN || item == Items.SPRUCE_SIGN || item == Items.BIRCH_SIGN ||
		       item == Items.JUNGLE_SIGN || item == Items.ACACIA_SIGN || item == Items.DARK_OAK_SIGN ||
		       item == Items.CRIMSON_SIGN || item == Items.WARPED_SIGN || item == Items.MANGROVE_SIGN ||
		       item == Items.CHERRY_SIGN || item == Items.BAMBOO_SIGN;
	}
}
