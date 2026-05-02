package com.tristankechlo.toolleveling.network;

import com.google.auto.service.AutoService;
import com.tristankechlo.toolleveling.FabricToolLeveling;
import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;
import com.tristankechlo.toolleveling.network.packets.SetEnchantmentToolLevelingTable;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.enchantment.Enchantment;

@AutoService(ServerBoundPacketHandler.class)
public final class FabricClientPacketHandler implements ServerBoundPacketHandler {

    public static void registerPacketHandler() {
        ClientPlayNetworking.registerGlobalReceiver(FabricToolLeveling.CHANNEL_CONFIG, FabricClientPacketHandler::handleConfigSync);
        ClientPlayNetworking.registerGlobalReceiver(FabricToolLeveling.CHANNEL_ITEM_VALUES, FabricClientPacketHandler::handleOpenItemValues);
    }

    // ####################
    // SENDING PACKETS
    // ####################

    @Override
    public void enchantAtToolLevelingTable(BlockPos pos, Enchantment enchantment, int level) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        SetEnchantmentToolLevelingTable msg = new SetEnchantmentToolLevelingTable(pos, enchantment, level);
        ClientPlayNetworking.send(FabricToolLeveling.CHANNEL_ENCHANT, msg.writeToBuffer(buf));
    }

    // ####################
    // HANDLING PACKETS
    // ####################

    static void handleConfigSync(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        SyncToolLevelingConfig msg = SyncToolLevelingConfig.decode(buf);
        client.execute(() -> SyncToolLevelingConfig.handle(msg));
    }

    static void handleOpenItemValues(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender sender) {
        client.execute(() -> client.setScreen(new ItemValueScreen()));
    }

}
