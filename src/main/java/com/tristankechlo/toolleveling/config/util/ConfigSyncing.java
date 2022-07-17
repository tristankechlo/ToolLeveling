package com.tristankechlo.toolleveling.config.util;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;

public final class ConfigSyncing {

    public static void syncAllConfigsToOneClient(ServerPlayer player) {
        Connection connection = player.connection.getConnection();
        for (Map.Entry<String, Config> element : ConfigManager.CONFIGS.entrySet()) {
            Config config = element.getValue();
            String identifier = element.getKey();
            JsonObject json = config.serialize(new JsonObject());
            PacketHandler.INSTANCE.sendTo(new SyncToolLevelingConfig(identifier, json), connection, NetworkDirection.PLAY_TO_CLIENT);
        }
    }

    public static void syncOneConfigToAllClients(String identifier, Config config) {
        JsonObject json = config.serialize(new JsonObject());
        PacketHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncToolLevelingConfig(identifier, json));
    }

    public static boolean deserializeConfig(String identifier, JsonObject json) {
        Config config = ConfigManager.CONFIGS.get(identifier);
        if (config == null) {
            return false;
        }
        config.deserialize(json);
        return true;
    }

}
