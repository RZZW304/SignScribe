package com.signscribe;

import com.signscribe.gui.SignScribeConfigScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class SignScribeConfig {
	private static SignScribeConfig INSTANCE;
	private static Path configPath;

	public boolean autoAdvance = true;
	public int autoAdvanceDelay = 20;
	public boolean showPreview = true;
	public boolean requireEmptyHand = true;
	public boolean placeSignsInstantly = false;
	public boolean showSuccessMessage = true;

	public static SignScribeConfig getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new SignScribeConfig();
		}
		return INSTANCE;
	}

	public static void setConfigPath(Path configDir) {
		configPath = configDir.resolve("signscribe.properties");
	}

	public static void load() {
		if (configPath == null || !Files.exists(configPath)) {
			return;
		}

		try (FileInputStream fis = new FileInputStream(configPath.toFile())) {
			Properties props = new Properties();
			props.load(fis);

			SignScribeConfig config = getInstance();
			config.autoAdvance = Boolean.parseBoolean(props.getProperty("autoAdvance", "true"));
			config.autoAdvanceDelay = Integer.parseInt(props.getProperty("autoAdvanceDelay", "20"));
			config.showPreview = Boolean.parseBoolean(props.getProperty("showPreview", "true"));
			config.requireEmptyHand = Boolean.parseBoolean(props.getProperty("requireEmptyHand", "true"));
			config.placeSignsInstantly = Boolean.parseBoolean(props.getProperty("placeSignsInstantly", "false"));
			config.showSuccessMessage = Boolean.parseBoolean(props.getProperty("showSuccessMessage", "true"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void save() {
		if (configPath == null) {
			return;
		}

		SignScribeConfig config = getInstance();
		Properties props = new Properties();

		props.setProperty("autoAdvance", String.valueOf(config.autoAdvance));
		props.setProperty("autoAdvanceDelay", String.valueOf(config.autoAdvanceDelay));
		props.setProperty("showPreview", String.valueOf(config.showPreview));
		props.setProperty("requireEmptyHand", String.valueOf(config.requireEmptyHand));
		props.setProperty("placeSignsInstantly", String.valueOf(config.placeSignsInstantly));
		props.setProperty("showSuccessMessage", String.valueOf(config.showSuccessMessage));

		try (FileOutputStream fos = new FileOutputStream(configPath.toFile())) {
			props.store(fos, "SignScribe Configuration");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Environment(EnvType.CLIENT)
	public Screen getConfigScreen(Screen parent) {
		return new SignScribeConfigScreen(parent);
	}
}
