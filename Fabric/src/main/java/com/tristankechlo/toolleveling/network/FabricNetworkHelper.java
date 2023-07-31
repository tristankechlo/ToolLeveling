package com.tristankechlo.toolleveling.network;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import com.tristankechlo.toolleveling.network.packets.TableUpgradeProcess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class FabricNetworkHelper implements NetworkHelper {

    @Override
    public void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(TableUpgradeProcess.CHANNEL_ID,
                (server, player, listener, buf, sender) -> handleOnServer(server, player, buf, TableUpgradeProcess::decode, TableUpgradeProcess::handle));
        ClientPlayNetworking.registerGlobalReceiver(SyncToolLevelingConfig.CHANNEL_ID,
                (client, listener, buf, sender) -> handleOnClient(client, buf, SyncToolLevelingConfig::decode, SyncToolLevelingConfig::handle));
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
        TableUpgradeProcess.encode(buf, pos);
        ClientPlayNetworking.send(TableUpgradeProcess.CHANNEL_ID, buf);
    }

    @Override
    public void syncToolLevelingConfig(ServerPlayer player, String identifier, JsonObject json) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        SyncToolLevelingConfig.encode(buf, identifier, json);
        ServerPlayNetworking.send(player, SyncToolLevelingConfig.CHANNEL_ID, buf);
    }

    @Override
    public void syncToolLevelingConfigToAllClients(MinecraftServer server, String identifier, JsonObject json) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        SyncToolLevelingConfig.encode(buf, identifier, json);
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, SyncToolLevelingConfig.CHANNEL_ID, buf);
        }
    }

    /**
     * generic method to handle packets on the server,
     * decodes the buffer and calls the actual packet handler
     */
    private static <MSG> void handleOnServer(MinecraftServer server, ServerPlayer player, FriendlyByteBuf buf, PacketDecoder<MSG> decoder, ServerSidePacketHandler<MSG> handler) {
        if (player == null) {
            return;
        }
        MSG msg = decoder.decode(buf);
        server.execute(() -> handler.handle(msg, player.getLevel()));
    }

    /**
     * generic method to handle packets on the client,
     * decodes the buffer and calls the actual packet handler
     */
    private static <MSG> void handleOnClient(Minecraft client, FriendlyByteBuf buf, PacketDecoder<MSG> decoder, ClientSidePacketHandler<MSG> handler) {
        if (client == null) {
            return;
        }
        MSG msg = decoder.decode(buf);
        client.execute(() -> handler.handle(msg));
    }

}
