package com.tristankechlo.toolleveling.network;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;

public class ForgeServerNetworkHelper implements ServerNetworkHelper {

    @Override
    public void openMenu(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof ToolLevelingTableBlockEntity) {
            NetworkHooks.openScreen((ServerPlayer) player, (ToolLevelingTableBlockEntity) blockentity, buf -> {
                buf.writeBlockPos(pos);
            });
        }
    }

    @Override
    public void syncToolLevelingConfig(ServerPlayer player, String identifier, JsonObject json) {
        Connection connection = player.connection.connection;
        ForgeNetworkHelper.INSTANCE.sendTo(new SyncToolLevelingConfig(identifier, json), connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public void syncToolLevelingConfigToAllClients(MinecraftServer server, String identifier, JsonObject json) {
        ForgeNetworkHelper.INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncToolLevelingConfig(identifier, json));
    }

}
