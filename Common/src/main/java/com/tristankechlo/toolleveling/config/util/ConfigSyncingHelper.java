package com.tristankechlo.toolleveling.config.util;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.NetworkHelper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class ConfigSyncingHelper {

    public static void syncAllConfigsToOneClient(ServerPlayer player) {
        for (AbstractConfig config : ConfigManager.CONFIGS) {
            String identifier = config.getFileName();
            JsonObject json = config.serialize();
            ToolLeveling.LOGGER.info("Sending config '{}' to player '{}'", config.getFileName(), player.getName().getString());
            NetworkHelper.INSTANCE.syncToolLevelingConfig(player, identifier, json);
        }
    }

    public static void syncOneConfigToAllClients(MinecraftServer server, AbstractConfig config) {
        JsonObject json = config.serialize();
        ToolLeveling.LOGGER.info("Sending config '{}' to all players", config.getFileName());
        NetworkHelper.INSTANCE.syncToolLevelingConfigToAllClients(server, config.getFileName(), json);
    }

    public static boolean deserializeConfig(String identifier, JsonObject json) {
        for (AbstractConfig config : ConfigManager.CONFIGS) {
            if (config.getFileName().equals(identifier)) {
                config.deserialize(json);
                return true;
            }
        }
        return false;
    }

}

