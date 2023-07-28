package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.packets.StartUpgradeProcess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class FabricNetworkHelper implements NetworkHelper {

    private static final ResourceLocation START_UPGRADE_PROCESS = new ResourceLocation(ToolLeveling.MOD_ID, "tool_leveling_table");

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(START_UPGRADE_PROCESS, FabricNetworkHelper::handleStartUpgradeProcess);
    }

    @Override
    public void openMenu(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        MenuProvider provider = state.getMenuProvider(level, pos);
        if (provider != null) {
            player.openMenu(provider);
        }
    }

    @Override
    public void startUpgradeProcess(BlockPos pos) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        StartUpgradeProcess.encode(new StartUpgradeProcess(pos), buf);
        ClientPlayNetworking.send(START_UPGRADE_PROCESS, buf);
    }

    static void handleStartUpgradeProcess(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        if (player == null) {
            return;
        }
        StartUpgradeProcess msg = StartUpgradeProcess.decode(buf);
        server.execute(() -> StartUpgradeProcess.handle(msg, player.getLevel()));
    }

}
