package com.tristankechlo.toolleveling.network.packets;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.config.util.ConfigSyncingHelper;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

public record SyncToolLevelingConfig(String identifier, JsonObject json) {

    public static final ResourceLocation CHANNEL_ID = new ResourceLocation(ToolLeveling.MOD_ID, "sync_tool_leveling_config");

    // forge specific method for packet encoding
    public static void encode(SyncToolLevelingConfig msg, FriendlyByteBuf buf) {
        encode(buf, msg.identifier(), msg.json());
    }

    public static void encode(FriendlyByteBuf buf, String identifier, JsonObject json) {
        buf.writeUtf(identifier);
        buf.writeUtf(ConfigManager.GSON.toJson(json));
    }

    public static SyncToolLevelingConfig decode(FriendlyByteBuf buf) {
        String identifier = buf.readUtf();
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(buf.readUtf()).getAsJsonObject();
        return new SyncToolLevelingConfig(identifier, json);
    }

    public static void handle(SyncToolLevelingConfig msg) {
        boolean check = ConfigSyncingHelper.deserializeConfig(msg.identifier(), msg.json());
        if (!check) {
            ToolLeveling.LOGGER.error("Config '{}' could not be loaded", msg.identifier());
            throw new RuntimeException("Config " + msg.identifier + " could not be loaded");
        }
        ToolLeveling.LOGGER.info("Config '{}' received and loaded.", msg.identifier());
    }

}
