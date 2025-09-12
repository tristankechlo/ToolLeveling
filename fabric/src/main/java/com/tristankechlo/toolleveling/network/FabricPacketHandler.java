package com.tristankechlo.toolleveling.network;

import com.google.auto.service.AutoService;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.packets.OpenItemValueScreenPacket;
import com.tristankechlo.toolleveling.network.packets.SetEnchantmentToolLevelingTable;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoService(IPacketHandler.class)
public final class FabricPacketHandler implements IPacketHandler {

    private static final ResourceLocation CHANNEL_ENCHANT = new ResourceLocation(ToolLeveling.MOD_ID, "upgrade");
    private static final ResourceLocation CHANNEL_CONFIG = new ResourceLocation(ToolLeveling.MOD_ID, "config");
    private static final ResourceLocation CHANNEL_ITEM_VALUES = new ResourceLocation(ToolLeveling.MOD_ID, "item_values");

    public static void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(CHANNEL_ENCHANT, FabricPacketHandler::handleEnchantAtToolLevelingTable);
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_CONFIG, FabricPacketHandler::handleConfigSync);
        ClientPlayNetworking.registerGlobalReceiver(CHANNEL_ITEM_VALUES, FabricPacketHandler::handleOpenItemValues);
    }

    // ####################
    // SENDING PACKETS
    // ####################

    @Override
    public void enchantAtToolLevelingTable(BlockPos pos, Enchantment enchantment, int level) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        SetEnchantmentToolLevelingTable msg = new SetEnchantmentToolLevelingTable(pos, enchantment, level);
        ClientPlayNetworking.send(CHANNEL_ENCHANT, msg.writeToBuffer(buf));
    }

    @Override
    public void syncOneConfigToOneClient(ServerPlayer player, String identifier, JsonElement json) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        SyncToolLevelingConfig.encode(new SyncToolLevelingConfig(identifier, json), buf);
        ServerPlayNetworking.send(player, CHANNEL_CONFIG, buf);
    }

    @Override
    public void syncOneConfigToAllClients(ServerLevel level, String identifier, JsonElement json) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        SyncToolLevelingConfig.encode(new SyncToolLevelingConfig(identifier, json), buf);
        for (ServerPlayer player : level.players()) {
            ServerPlayNetworking.send(player, CHANNEL_CONFIG, buf);
        }
    }

    @Override
    public void openItemValueScreen(ServerPlayer player) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        OpenItemValueScreenPacket.encode(new OpenItemValueScreenPacket(), buf);
        ServerPlayNetworking.send(player, CHANNEL_ITEM_VALUES, buf);
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

    static void handleConfigSync(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        SyncToolLevelingConfig msg = SyncToolLevelingConfig.decode(buf);
        client.execute(() -> SyncToolLevelingConfig.handle(msg));
    }

    static void handleOpenItemValues(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        client.execute(() -> OpenItemValueScreenPacket.handle(client));
    }

}
