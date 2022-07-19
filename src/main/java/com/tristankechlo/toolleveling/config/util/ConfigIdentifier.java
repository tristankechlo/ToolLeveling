package com.tristankechlo.toolleveling.config.util;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.config.CommandConfig;
import com.tristankechlo.toolleveling.config.ItemValueConfig;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.util.StringIdentifiable;

import java.util.function.Consumer;
import java.util.function.Function;

public enum ConfigIdentifier implements StringIdentifiable {

    GENERAL("general", "tool_leveling_table.json", ToolLevelingConfig::setToDefault, ToolLevelingConfig::serialize, ToolLevelingConfig::deserialize),
    ITEM_VALUES("item_values", "item_values.json", ItemValueConfig::setToDefault, ItemValueConfig::serialize, ItemValueConfig::deserialize),
    COMMANDS("commands", "command_config.json", CommandConfig::setToDefault, CommandConfig::serialize, CommandConfig::deserialize);

    @SuppressWarnings("deprecation")
    public static final Codec<ConfigIdentifier> CODEC;
    private final String name;
    private final String fileName;
    private final Runnable resetter;
    private final Function<JsonObject, JsonObject> serializer;
    private final Consumer<JsonObject> deserializer;

    ConfigIdentifier(String name, String fileName, Runnable resetter,
                     Function<JsonObject, JsonObject> serializer, Consumer<JsonObject> deserializer) {
        this.name = name;
        this.fileName = fileName;
        this.resetter = resetter;
        this.serializer = serializer;
        this.deserializer = deserializer;
    }

    @Override
    public String asString() {
        return this.toString();
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

    static {
        CODEC = StringIdentifiable.createCodec(ConfigIdentifier::values);
    }

}
