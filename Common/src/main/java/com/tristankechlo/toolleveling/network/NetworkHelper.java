package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface NetworkHelper {

    NetworkHelper INSTANCE = Services.load(NetworkHelper.class);

    static void setup(){
        NetworkHelper.INSTANCE.registerPackets();
        ToolLeveling.LOGGER.info("Packets registered");
    }

    void registerPackets();

    void openMenu(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit);

    /**
     * Sends a packet to the server to start the upgrade process
     *
     * @param pos the position of the Tool Leveling Table
     */
    void startUpgradeProcess(BlockPos pos);

    @FunctionalInterface
    interface PacketHandler<T> {
        void handle(T msg, ServerLevel level);
    }

    @FunctionalInterface
    interface PacketDecoder<T> {
        T decode(FriendlyByteBuf buf);
    }

}
