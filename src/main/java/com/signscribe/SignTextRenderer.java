package com.signscribe;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.text.Text;

public class SignTextRenderer {
	
	public static void applyTextToSign(SignBlockEntity signEntity, SignPage signPage) {
		if (signEntity == null || signPage == null) {
			return;
		}
		
		String[] lines = signPage.getLines();
		for (int i = 0; i < Math.min(lines.length, 4); i++) {
			Text lineText = Text.literal(lines[i]);
			signEntity.setTextOnRow(i, lineText);
		}
		
		signEntity.markDirty();
	}
	
	public static void clearSignText(SignBlockEntity signEntity) {
		if (signEntity == null) {
			return;
		}
		
		for (int i = 0; i < 4; i++) {
			signEntity.setTextOnRow(i, Text.empty());
		}
		
		signEntity.markDirty();
	}
	
	public static String[] getSignText(SignBlockEntity signEntity) {
		if (signEntity == null) {
			return new String[]{"", "", "", ""};
		}
		
		String[] lines = new String[4];
		for (int i = 0; i < 4; i++) {
			Text text = signEntity.getText(i).getMessage();
			lines[i] = text != null ? text.getString() : "";
		}
		
		return lines;
	}
	
	public static boolean isSignEmpty(SignBlockEntity signEntity) {
		if (signEntity == null) {
			return true;
		}
		
		for (int i = 0; i < 4; i++) {
			Text text = signEntity.getText(i).getMessage();
			if (text != null && !text.getString().isEmpty()) {
				return false;
			}
		}
		
		return true;
	}
	
	public static int getNonEmptyLineCount(SignBlockEntity signEntity) {
		if (signEntity == null) {
			return 0;
		}
		
		int count = 0;
		for (int i = 0; i < 4; i++) {
			Text text = signEntity.getText(i).getMessage();
			if (text != null && !text.getString().isEmpty()) {
				count++;
			}
		}
		
		return count;
	}
}
