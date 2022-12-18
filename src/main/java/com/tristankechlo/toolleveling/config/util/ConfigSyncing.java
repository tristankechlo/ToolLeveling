package com.tristankechlo.toolleveling.config.util;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

public final class ConfigSyncing {

    public static void syncAllConfigsToOneClient(ServerPlayer player) {
        Connection connection = player.connection.getConnection();
        for (ConfigIdentifier config : ConfigIdentifier.values()) {
            String identifier = config.withModID();
            JsonObject json = config.serialize(new JsonObject());
            ToolLeveling.LOGGER.info("Sending config to client: '{}'", config.withModID());
            PacketHandler.INSTANCE.sendTo(new SyncToolLevelingConfig(identifier, json), connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void syncOneConfigToAllClients(ConfigIdentifier config) {
        JsonObject json = config.serialize(new JsonObject());
        ToolLeveling.LOGGER.info("Sending config to all clients: '{}'", config.withModID());
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncToolLevelingConfig(config.withModID(), json));
    }

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
