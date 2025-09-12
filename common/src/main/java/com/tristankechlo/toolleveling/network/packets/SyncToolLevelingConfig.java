package com.tristankechlo.toolleveling.network.packets;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.ConfigSyncing;
import net.minecraft.network.FriendlyByteBuf;

public record SyncToolLevelingConfig(String identifier, JsonElement json) {

    public static void encode(SyncToolLevelingConfig msg, FriendlyByteBuf buffer) {
        buffer.writeUtf(msg.identifier);
        buffer.writeUtf(new Gson().toJson(msg.json));
    }

    public static SyncToolLevelingConfig decode(FriendlyByteBuf buffer) {
        String identifier = buffer.readUtf();
        JsonElement json = new JsonParser().parse(buffer.readUtf());
        return new SyncToolLevelingConfig(identifier, json);
    }

    public static void handle(SyncToolLevelingConfig msg) {
        ToolLeveling.LOGGER.info("Received config from server: '{}'", msg.identifier);
        ConfigSyncing.deserializeConfig(msg.identifier, msg.json);
    }

}
