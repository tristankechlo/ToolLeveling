package com.tristankechlo.toolleveling.config.util;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.config.CommandConfig;
import com.tristankechlo.toolleveling.config.ItemValueConfig;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import com.tristankechlo.toolleveling.utils.Names;

import java.util.function.Consumer;
import java.util.function.Function;

public enum ConfigIdentifier {

    GENERAL("general", "tool_leveling_table.json", Names.URLS.CONFIG_INFO_GENERAL, ToolLevelingConfig::setToDefault, ToolLevelingConfig::serialize, ToolLevelingConfig::deserialize),
    ITEM_VALUES("item_values", "item_values.json", Names.URLS.CONFIG_INFO_ITEM_VALUES, ItemValueConfig::setToDefault, ItemValueConfig::serialize, ItemValueConfig::deserialize),
    COMMANDS("commands", "command_config.json", Names.URLS.CONFIG_INFO_COMMANDS, CommandConfig::setToDefault, CommandConfig::serialize, CommandConfig::deserialize);

    private final String name;
    private final String fileName;
    private final String infoUrl;
    private final Runnable resetter;
    private final Function<JsonObject, JsonObject> serializer;
    private final Consumer<JsonObject> deserializer;

    ConfigIdentifier(String name, String fileName, String url, Runnable resetter,
                     Function<JsonObject, JsonObject> serializer, Consumer<JsonObject> deserializer) {
        this.name = name;
        this.fileName = fileName;
        this.infoUrl = url;
        this.resetter = resetter;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    public String withModID() {
        return Names.MOD_ID + ":" + this.name;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setToDefault() {
        this.resetter.run();
    }

    public JsonObject serialize(JsonObject json) {
        return this.serializer.apply(json);
    }

    public void deserialize(JsonObject json) {
        this.deserializer.accept(json);
    }

    public String getInfoUrl() {
        return this.infoUrl;
    }

}
