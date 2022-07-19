package com.tristankechlo.toolleveling.config.util;

import com.google.gson.*;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public final class ConfigManager {

    private static final File CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve("toolleveling").toFile();
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

    public static void reloadAllConfigs(MinecraftServer server) {
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
            ConfigSyncing.syncOneConfigToAllClients(server, config);
        }
    }

    public static void resetOneConfig(MinecraftServer server, ConfigIdentifier config) {
        config.setToDefault();
        File configFile = new File(CONFIG_DIR, config.getFileName());
        ConfigManager.writeConfigToFile(config, configFile);
        ToolLeveling.LOGGER.info("Config '{}' was set to default.", config.getFileName());
        ConfigSyncing.syncOneConfigToAllClients(server, config);
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
            JsonElement jsonElement = JsonParser.parseReader(new FileReader(file));
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
        lines.add("General information about the config files");
        lines.add("============================================================");
        lines.add("");
        lines.add("Before loading the config, always make sure the file has the correct syntax.");
        lines.add("If you try to load a config with invalid syntax, the config will reset to its default values.");
        lines.add("You can check if your config is valid, by pasting the complete file to https://jsonlint.com/");
        lines.add("");
        lines.add("If you want to reset a config, just delete the file and reload the game.");
        lines.add("");
        lines.add("");
        lines.add("============================================================");
        lines.add("Configuration files");
        lines.add("============================================================");
        lines.add("");
        lines.add("----------- tool_leveling_table.json ------------");
        lines.add("This config file contains all options that are used in the tool leveling table.");
        lines.add("Full explanation for this config https://gist.github.com/tristankechlo/3818a20804ba69809422de42bf51ee81");
        lines.add("The default config can be found here https://gist.github.com/tristankechlo/43d78f0caa16a1f52da45c7cc459d0b7");
        lines.add("-------------------------------------------------");
        lines.add("");
        lines.add("--------------- item_values.json ----------------");
        lines.add("In this config file you can define the worth of each item.");
        lines.add("Full explanation for this config https://gist.github.com/tristankechlo/31903eaca2c3f05cde7223cb2c19b416");
        lines.add("The default config can be found here https://gist.github.com/tristankechlo/c1bde77a67b56c2cb3ca3c94005a93df");
        lines.add("-------------------------------------------------");
        lines.add("");
        lines.add("--------------- command_config.json -------------");
        lines.add("This config file contains all options that are used in the commands.");
        lines.add("Full explanation for this config https://gist.github.com/tristankechlo/246643b3c94d8ad4aabfc37165a891d7");
        lines.add("The default config can be found here https://gist.github.com/tristankechlo/26a3e2ff47ab0306dc8950dfe38ce8ee");
        lines.add("-------------------------------------------------");
        lines.add("");
        return lines;
    }

}
