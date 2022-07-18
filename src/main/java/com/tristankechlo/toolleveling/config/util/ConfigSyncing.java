package com.tristankechlo.toolleveling.config.util;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.network.ServerNetworkHandler;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Map;

public final class ConfigSyncing {

    public static void syncAllConfigsToOneClient(ServerPlayerEntity player) {
        for (Map.Entry<String, ConfigHandler> element : ConfigManager.CONFIGS.entrySet()) {
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
        ConfigHandler config = ConfigManager.CONFIGS.get(identifier);
        if (config == null) {
            return false;
        }
        config.deserialize(json);
        return true;
    }

}
