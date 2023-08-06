package com.tristankechlo.toolleveling.network;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface ServerNetworkHelper {

    ServerNetworkHelper INSTANCE = Services.load(ServerNetworkHelper.class);

    static void setup() {
        INSTANCE.registerPacketReceiver();
        ToolLeveling.LOGGER.info("ClientSidePacketHandler registered");
    }

    default void registerPacketReceiver() {}

    void openMenu(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit);

    /**
     * Sends a packet to the client to sync one config to one player
     */
    void syncToolLevelingConfig(ServerPlayer player, String identifier, JsonObject json);

    /**
     * Sends a packet to all clients to sync one config to all players
     */
    void syncToolLevelingConfigToAllClients(MinecraftServer server, String identifier, JsonObject json);

}
