package com.tristankechlo.toolleveling.config.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonWriter;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.platform.IPlatformHelper;
import com.tristankechlo.toolleveling.utils.ProjectLinks;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ConfigManager {

    private static final File CONFIG_DIR = IPlatformHelper.INSTANCE.getConfigDirectory().resolve(ToolLeveling.MOD_ID).toFile();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().serializeNulls().disableHtmlEscaping().create();
    public static final Map<String, ConfigIdentifier<?>> CONFIGS = Map.of(
            "general", ConfigIdentifier.GENERAL,
            "item_values", ConfigIdentifier.ITEM_VALUES
    );

    public static void loadAndVerifyConfigs() {
        for (ConfigIdentifier<?> config : CONFIGS.values()) {
            File configFile = new File(CONFIG_DIR, config.getFileName());

            if (!configFile.exists()) {
                config.setToDefault();
                ConfigManager.writeConfigToFile(config, configFile);
                ToolLeveling.LOGGER.warn("No config '{}' was found, created a new one.", config.getFileName());
                continue;
            }

            try {
                ConfigManager.loadConfigFromFile(config, configFile);
                ToolLeveling.LOGGER.info("Config '{}' was successfully loaded.", config.getFileName());
            } catch (Exception e) {
                ToolLeveling.LOGGER.error(e.getMessage());
                ToolLeveling.LOGGER.error("Error loading config '{}', config hasn't been loaded. Using default config.", config.getFileName());
                config.setToDefault();
            }
        }
    }

    private static void writeConfigToFile(ConfigIdentifier<?> config, File file) {
        try {
            JsonElement jsonObject = config.serialize();
            JsonWriter writer = new JsonWriter(new FileWriter(file));
            writer.setIndent("\t");
            GSON.toJson(jsonObject, writer);
            writer.close();
        } catch (Exception e) {
            ToolLeveling.LOGGER.error("There was an error writing the config to file: '{}'", config.getFileName());
            ToolLeveling.LOGGER.error(e.getMessage());
        }
    }

    private static void loadConfigFromFile(ConfigIdentifier<?> config, File file) throws FileNotFoundException {
        JsonParser parser = new JsonParser();
        JsonElement json = parser.parse(new FileReader(file));
        config.deserialize(json);
    }

    public static void resetAllConfigs() {
        for (ConfigIdentifier<?> config : CONFIGS.values()) {
            config.setToDefault();
            File configFile = new File(CONFIG_DIR, config.getFileName());
            ConfigManager.writeConfigToFile(config, configFile);
            ToolLeveling.LOGGER.info("Config '{}' was set to default.", config.getFileName());
        }
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
        lines.add("The wiki is located at: " + ProjectLinks.WIKI.url);
        lines.add("");
        return lines;
    }

}
