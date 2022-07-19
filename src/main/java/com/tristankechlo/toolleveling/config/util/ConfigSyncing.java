package com.tristankechlo.toolleveling.config.util;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.ServerNetworkHandler;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public final class ConfigSyncing {

    //executed on the server side
    public static void syncAllConfigsToOneClient(ServerPlayerEntity player) {
        for (ConfigIdentifier config : ConfigIdentifier.values()) {
            JsonObject json = config.serialize(new JsonObject());
            ToolLeveling.LOGGER.info("Sending config to client: '{}'", config.withModID());
            ServerNetworkHandler.sendSyncConfig(player, config, json);
        }
    }

    //executed on the server side
    public static void syncOneConfigToAllClients(MinecraftServer server, ConfigIdentifier config) {
        JsonObject json = config.serialize(new JsonObject());
        for (ServerPlayerEntity player : PlayerLookup.all(server)) {
            ToolLeveling.LOGGER.info("Sending config to client: '{}'", config.withModID());
            ServerNetworkHandler.sendSyncConfig(player, config, json);
        }
    }

    //executed on the client side
    public static boolean deserializeConfig(String identifier, JsonObject json) {
        for (ConfigIdentifier config : ConfigIdentifier.values()) {
            if (config.withModID().equals(identifier)) {
                config.deserialize(json);
                return true;
            }
        }
        return false;
    }

}
