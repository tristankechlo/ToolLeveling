package com.tristankechlo.toolleveling.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.ServerNetworkHandler;
import com.tristankechlo.toolleveling.utils.Names;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ConfigManager {

	private static final Map<String, ConfigHandler> CONFIGS = getConfigList();
	private static final File ConfigDir = FabricLoader.getInstance().getConfigDir().resolve("toolleveling").toFile();

	private ConfigManager() {}

	public static void setup() {
		if (!ConfigDir.exists()) {
			ConfigDir.mkdirs();
		}
		for (Map.Entry<String, ConfigHandler> element : CONFIGS.entrySet()) {
			ConfigHandler config = element.getValue();
			config.setToDefault();
			File configFile = new File(ConfigDir, config.getFileName());
			if (configFile.exists()) {
				ConfigManager.loadConfigFromFile(config, configFile);
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.info("Saved the checked/corrected config: " + element.getKey());
			} else {
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.warn("No config[" + element.getKey() + "] was found, created a new one.");
			}
		}
	}

	public static void reloadAllConfigs(MinecraftServer server) {
		if (!ConfigDir.exists()) {
			ConfigDir.mkdirs();
		}
		for (Map.Entry<String, ConfigHandler> element : CONFIGS.entrySet()) {
			ConfigHandler config = element.getValue();
			File configFile = new File(ConfigDir, config.getFileName());
			if (configFile.exists()) {
				ConfigManager.loadConfigFromFile(config, configFile);
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.info("Saved the checked/corrected config: " + element.getKey());
			} else {
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.warn("No config [" + element.getKey() + "] was found, created a new one.");
			}
			syncOneConfigToAllClients(server, element.getKey(), config);
		}
	}

	public static void resetAllConfigs(MinecraftServer server) {
		if (!ConfigDir.exists()) {
			ConfigDir.mkdirs();
		}
		for (Map.Entry<String, ConfigHandler> element : CONFIGS.entrySet()) {
			resetOneConfig(server, element.getKey(), element.getValue());
		}
	}

	public static void resetOneConfig(MinecraftServer server, String identifier) {
		if (!ConfigDir.exists()) {
			ConfigDir.mkdirs();
		}
		ConfigHandler config = CONFIGS.get(identifier);
		if (config != null) {
			resetOneConfig(server, identifier, config);
		}
	}

	private static void resetOneConfig(MinecraftServer server, String identifier, ConfigHandler config) {
		config.setToDefault();
		File configFile = new File(ConfigDir, config.getFileName());
		ConfigManager.writeConfigToFile(config, configFile);
		ToolLeveling.LOGGER.info("Config [" + identifier + "] was set to default.");
		syncOneConfigToAllClients(server, identifier, config);
	}

	private static void writeConfigToFile(ConfigHandler config, File file) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("_commentSyntax", "to check if your config-file has the correct "
				+ "syntax, test your configuration on this website: https://jsonlint.com/");
		jsonObject = config.serialize(jsonObject);
		String jsonString = ConfigManager.getGson().toJson(jsonObject);
		try {
			FileWriter writer = new FileWriter(file);
			writer.write(jsonString);
			writer.close();
		} catch (Exception e) {
			ToolLeveling.LOGGER.error("There was an error writing the config to file: " + config.getFileName());
			e.printStackTrace();
		}
	}

	private static void loadConfigFromFile(ConfigHandler config, File file) {
		JsonObject json = null;
		try {
			JsonElement jsonElement = JsonParser.parseReader(new FileReader(file));
			json = jsonElement.getAsJsonObject();
		} catch (Exception e) {
			ToolLeveling.LOGGER.error("There was an error loading the config file: " + config.getFileName());
			e.printStackTrace();
		}
		if (json != null) {
			config.deserialize(json);
			ToolLeveling.LOGGER.info("Config[" + config.getFileName() + "] was successfully loaded.");
		} else {
			ToolLeveling.LOGGER.error("Error loading config[" + config.getFileName() + "], config hasn't been loaded.");
		}
	}

	public static void syncAllConfigsToOneClient(ServerPlayerEntity player) {
		for (Map.Entry<String, ConfigHandler> element : CONFIGS.entrySet()) {
			ConfigHandler config = element.getValue();
			String identifier = element.getKey();
			JsonObject json = config.serialize(new JsonObject());
			ServerNetworkHandler.sendSyncConfig(player, identifier, json);
		}
	}

	public static void syncOneConfigToAllClients(MinecraftServer server, String identifier, ConfigHandler config) {
		JsonObject json = config.serialize(new JsonObject());
		for (ServerPlayerEntity player : PlayerLookup.all(server)) {
			ServerNetworkHandler.sendSyncConfig(player, identifier, json);
		}
	}

	public static boolean deserializeConfig(String identifier, JsonObject json) {
		if (!CONFIGS.containsKey(identifier)) {
			return false;
		}
		ConfigHandler config = CONFIGS.get(identifier);
		config.deserialize(json);
		return true;
	}

	public static Gson getGson() {
		GsonBuilder gson = new GsonBuilder();
		gson.setPrettyPrinting();
		gson.serializeNulls();
		gson.disableHtmlEscaping();
		return gson.create();
	}

	private static Map<String, ConfigHandler> getConfigList() {
		Map<String, ConfigHandler> configs = new HashMap<>();

		configs.put(Names.MOD_ID + ":general",
				new ConfigHandler("toolleveling.json", ToolLevelingConfig::setToDefaultValues,
						ToolLevelingConfig::serialize, ToolLevelingConfig::deserialize));

		configs.put(Names.MOD_ID + ":item_values", new ConfigHandler("item_values.json", ItemValues::setToDefaultValues,
				ItemValues::serialize, ItemValues::deserialize));

		return configs;
	}

	public static boolean hasIdentifier(String identifier) {
		return CONFIGS.containsKey(identifier);
	}

	public static String getConfigFileName(String identifier) {
		return CONFIGS.get(identifier).getFileName();
	}

	public static String getConfigPath(String identifier) {
		File configFile = new File(ConfigDir, CONFIGS.get(identifier).getFileName());
		return configFile.getAbsolutePath();
	}

}
