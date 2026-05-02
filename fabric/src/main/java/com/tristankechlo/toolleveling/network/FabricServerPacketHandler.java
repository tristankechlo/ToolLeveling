package com.tristankechlo.toolleveling.network;

import com.google.auto.service.AutoService;
import com.google.gson.JsonElement;
import com.tristankechlo.toolleveling.FabricToolLeveling;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.packets.OpenItemValueScreenPacket;
import com.tristankechlo.toolleveling.network.packets.SetEnchantmentToolLevelingTable;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

@AutoService(ClientBoundPacketHandler.class)
public final class FabricServerPacketHandler implements ClientBoundPacketHandler {

    public static void registerPacketHandler() {
        ServerPlayNetworking.registerGlobalReceiver(FabricToolLeveling.CHANNEL_ENCHANT, FabricServerPacketHandler::handleEnchantAtToolLevelingTable);
    }

    // ####################
    // SENDING PACKETS
    // ####################

    @Override
    public void syncOneConfigToOneClient(ServerPlayer player, String identifier, JsonElement json) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        SyncToolLevelingConfig.encode(new SyncToolLevelingConfig(identifier, json), buf);
        ServerPlayNetworking.send(player, FabricToolLeveling.CHANNEL_CONFIG, buf);
    }

    @Override
    public void syncOneConfigToAllClients(ServerLevel level, String identifier, JsonElement json) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        SyncToolLevelingConfig.encode(new SyncToolLevelingConfig(identifier, json), buf);
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, FabricToolLeveling.CHANNEL_CONFIG, buf);
        }
    }

    @Override
    public void openItemValueScreen(ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        OpenItemValueScreenPacket.encode(new OpenItemValueScreenPacket(), buf);
        ServerPlayNetworking.send(player, FabricToolLeveling.CHANNEL_ITEM_VALUES, buf);
    }

    // ####################
    // HANDLING PACKETS
    // ####################

    static void handleEnchantAtToolLevelingTable(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender sender) {
        if (player == null) {
            ToolLeveling.LOGGER.error("Received SetEnchantmentToolLevelingTable packet from invalid player");
            return;
        }
        SetEnchantmentToolLevelingTable msg = SetEnchantmentToolLevelingTable.decode(buf);
        server.execute(() -> SetEnchantmentToolLevelingTable.handle(msg, player, (ServerLevel) player.level));
    }

}
