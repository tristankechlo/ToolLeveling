package com.tristankechlo.toolleveling.config.util;

import com.google.gson.JsonElement;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.ClientBoundPacketHandler;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;

public final class ConfigSyncing {

    public static void syncAllConfigsToAllClients(ServerLevel level) {
        for (Map.Entry<String, ConfigIdentifier<?>> entry : ConfigManager.CONFIGS.entrySet()) {
            JsonElement json = entry.getValue().serialize();
            ToolLeveling.LOGGER.info("Sending config to all clients: '{}'", entry.getKey());
            ClientBoundPacketHandler.INSTANCE.syncOneConfigToAllClients(level, entry.getKey(), json);
        }
    }

    public static void syncAllConfigsToOneClient(ServerPlayer player) {
        for (Map.Entry<String, ConfigIdentifier<?>> entry : ConfigManager.CONFIGS.entrySet()) {
            JsonElement json = entry.getValue().serialize();
            ToolLeveling.LOGGER.info("Sending config to client: '{}'", entry.getKey());
            ClientBoundPacketHandler.INSTANCE.syncOneConfigToOneClient(player, entry.getKey(), json);
        }
    }

    public static void deserializeConfig(String identifier, JsonElement json) {
        ConfigIdentifier<?> config = ConfigManager.CONFIGS.get(identifier);
        config.deserialize(json);
    }

}
