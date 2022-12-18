package com.tristankechlo.toolleveling.config.util;

import com.google.gson.*;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public final class ConfigManager {

	private static final File CONFIG_DIR = FMLPaths.CONFIGDIR.get().resolve("toolleveling").toFile();
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();

	private ConfigManager() {}

	public static void setup() {
		for (ConfigIdentifier config : ConfigIdentifier.values()) {
			config.setToDefault();
			File configFile = new File(CONFIG_DIR, config.getFileName());
			if (configFile.exists()) {
				ConfigManager.loadConfigFromFile(config, configFile);
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.info("Saved the checked/corrected config: " + config.getFileName());
			} else {
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.warn("No config '{}' was found, created a new one.", config.getFileName());
			}
		}
	}

	public static void reloadAllConfigs() {
		for (ConfigIdentifier config : ConfigIdentifier.values()) {
			File configFile = new File(CONFIG_DIR, config.getFileName());
			if (configFile.exists()) {
				ConfigManager.loadConfigFromFile(config, configFile);
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.info("Saved the checked/corrected config: " + config.getFileName());
			} else {
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.warn("No config '{}' was found, created a new one.", config.getFileName());
			}
			ConfigSyncing.syncOneConfigToAllClients(config);
		}
	}

	public static void resetOneConfig(ConfigIdentifier config) {
		config.setToDefault();
		File configFile = new File(CONFIG_DIR, config.getFileName());
		ConfigManager.writeConfigToFile(config, configFile);
		ToolLeveling.LOGGER.info("Config '{}' was set to default.", config.getFileName());
		ConfigSyncing.syncOneConfigToAllClients(config);
	}

	private static void writeConfigToFile(ConfigIdentifier config, File file) {
		JsonObject jsonObject = config.serialize(new JsonObject());
		String jsonString = GSON.toJson(jsonObject);
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(jsonString);
			writer.close();
		} catch (Exception e) {
			ToolLeveling.LOGGER.error("There was an error writing the config to file: " + config.getFileName());
			e.printStackTrace();
		}
	}

	private static void loadConfigFromFile(ConfigIdentifier config, File file) {
		JsonObject json = null;
		try {
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(new FileReader(file));
			json = jsonElement.getAsJsonObject();
		} catch (Exception e) {
			ToolLeveling.LOGGER.error("There was an error loading the config file: " + config.getFileName());
			e.printStackTrace();
		}
		if (json != null) {
			config.deserialize(json);
			ToolLeveling.LOGGER.info("Config '{}' was successfully loaded.", config.getFileName());
		} else {
			ToolLeveling.LOGGER.error("Error loading config '{}', config hasn't been loaded.", config.getFileName());
		}
	}

	public static String getConfigPath(ConfigIdentifier identifier) {
		File configFile = new File(CONFIG_DIR, identifier.getFileName());
		return configFile.getAbsolutePath();
	}

	public static void createConfigFolder() {
		if (!CONFIG_DIR.exists()) {
			if (!CONFIG_DIR.mkdirs()) {
				throw new RuntimeException("Could not create config folder: " + CONFIG_DIR.getAbsolutePath());
			}
		}
		//create README.txt
		try {
			FileWriter writer = new FileWriter(new File(CONFIG_DIR, "README.txt"));
			for (String line : ConfigManager.getReadmeContent()) {
				writer.write(line + "\n");
			}
			writer.close();
			ToolLeveling.LOGGER.info("Created README.txt in config folder.");
		} catch (Exception e) {
			ToolLeveling.LOGGER.error("Could not create README.txt in config folder: " + CONFIG_DIR.getAbsolutePath());
			e.printStackTrace();
		}
	}

	private static List<String> getReadmeContent() {
		List<String> lines = new ArrayList<>();
		lines.add("============================================================");
		lines.add("                        IMPORTANT");
		lines.add("============================================================");
		lines.add("");
		lines.add("Before editing the config, please take a look at the wiki.");
		lines.add("You can find information about all configs, and it's options there.");
		lines.add("The wiki is located at: " + Names.URLS.GITHUB_WIKI);
		lines.add("");
		return lines;
	}

}
