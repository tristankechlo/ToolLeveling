package com.tristankechlo.toolleveling.config;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.PacketDistributor;

public final class ConfigManager {

	private static final Map<String, Config> CONFIGS = getConfigList();
	private static final File ConfigDir = FMLPaths.CONFIGDIR.get().resolve("toolleveling").toFile();

	private ConfigManager() {}

	public static void setup() {
		if (!ConfigDir.exists()) {
			ConfigDir.mkdirs();
		}
		for (Map.Entry<String, Config> element : CONFIGS.entrySet()) {
			Config config = element.getValue();
			config.setToDefault();
			File configFile = new File(ConfigDir, config.getFileName());
			if (configFile.exists()) {
				ConfigManager.loadConfigFromFile(config, configFile);
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.log(Level.INFO, "Saved the checked/corrected config: " + element.getKey());
			} else {
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.log(Level.INFO,
						"No config[" + element.getKey() + "] was found, created a new one.");
			}
		}
	}

	public static void reloadAllConfigs() {
		if (!ConfigDir.exists()) {
			ConfigDir.mkdirs();
		}
		for (Map.Entry<String, Config> element : CONFIGS.entrySet()) {
			Config config = element.getValue();
			File configFile = new File(ConfigDir, config.getFileName());
			if (configFile.exists()) {
				ConfigManager.loadConfigFromFile(config, configFile);
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.log(Level.INFO, "Saved the checked/corrected config: " + element.getKey());
			} else {
				ConfigManager.writeConfigToFile(config, configFile);
				ToolLeveling.LOGGER.log(Level.INFO,
						"No config [" + element.getKey() + "] was found, created a new one.");
			}
			syncOneConfigToAllClients(element.getKey(), config);
		}
	}

	public static void resetAllConfigs() {
		if (!ConfigDir.exists()) {
			ConfigDir.mkdirs();
		}
		for (Map.Entry<String, Config> element : CONFIGS.entrySet()) {
			resetOneConfig(element.getKey(), element.getValue());
		}
	}

	public static void resetOneConfig(String identifier) {
		if (!ConfigDir.exists()) {
			ConfigDir.mkdirs();
		}
		Config config = CONFIGS.get(identifier);
		if (config != null) {
			resetOneConfig(identifier, config);
		}
	}

	private static void resetOneConfig(String identifier, Config config) {
		config.setToDefault();
		File configFile = new File(ConfigDir, config.getFileName());
		ConfigManager.writeConfigToFile(config, configFile);
		ToolLeveling.LOGGER.log(Level.INFO, "Saved [" + identifier + "] as new config.");
		syncOneConfigToAllClients(identifier, config);
	}

	private static void writeConfigToFile(Config config, File file) {
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
			ToolLeveling.LOGGER.log(Level.INFO,
					"There was an error writing the config to file: " + config.getFileName());
			e.printStackTrace();
		}
	}

	private static void loadConfigFromFile(Config config, File file) {
		JsonObject json = null;
		try {
			JsonParser parser = new JsonParser();
			JsonElement jsonElement = parser.parse(new FileReader(file));
			json = jsonElement.getAsJsonObject();
		} catch (Exception e) {
			ToolLeveling.LOGGER.log(Level.INFO, "There was an error loading the config file: " + config.getFileName());
			e.printStackTrace();
		}
		if (json != null) {
			config.deserialize(json);
			ToolLeveling.LOGGER.log(Level.INFO, "Config[" + config.getFileName() + "] was successfully loaded.");
		} else {
			ToolLeveling.LOGGER.log(Level.INFO,
					"Error loading config[" + config.getFileName() + "], config hasn't been loaded.");
		}
	}

	public static void syncAllConfigsToOneClient(ServerPlayerEntity player) {
		for (Map.Entry<String, Config> element : CONFIGS.entrySet()) {
			Config config = element.getValue();
			String identifier = element.getKey();
			JsonObject json = config.serialize(new JsonObject());
			PacketHandler.INSTANCE.sendTo(new SyncToolLevelingConfig(identifier, json),
					player.connection.getNetworkManager(), NetworkDirection.PLAY_TO_CLIENT);
		}
	}

	public static void syncOneConfigToAllClients(String identifier, Config config) {
		JsonObject json = config.serialize(new JsonObject());
		PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncToolLevelingConfig(identifier, json));
	}

	public static boolean deserializeConfig(String identifier, JsonObject json) {
		if (!CONFIGS.containsKey(identifier)) {
			return false;
		}
		Config config = CONFIGS.get(identifier);
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

	private static Map<String, Config> getConfigList() {
		Map<String, Config> configs = new HashMap<>();

		configs.put(Names.MOD_ID + ":general", new Config("toolleveling.json", ToolLevelingConfig::setToDefaultValues,
				ToolLevelingConfig::serialize, ToolLevelingConfig::deserialize));

		configs.put(Names.MOD_ID + ":itemValues", new Config("item_values.json", ItemValues::setToDefaultValues,
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
