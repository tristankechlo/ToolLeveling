package com.tristankechlo.toolleveling.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;
import com.tristankechlo.toolleveling.config.util.ConfigSyncing;
import com.tristankechlo.toolleveling.utils.Names.NetworkChannels;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

@Environment(EnvType.CLIENT)
public final class ClientNetworkHandler {

    public static void recieveOpenItemValues(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        client.execute(() -> {
            MinecraftClient.getInstance().setScreen(new ItemValueScreen());
        });
    }

    public static void sendSetEnchantmentLevel(BlockPos pos, Enchantment enchantment, int level) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBlockPos(pos);
        Identifier identifier = Registry.ENCHANTMENT.getId(enchantment);
        if (identifier == null) {
            ToolLeveling.LOGGER.warn("Error while encoding the packet for " + NetworkChannels.SET_ENCHANTMENT_LEVEL.toString());
        }
        buf.writeIdentifier(identifier);
        buf.writeInt(level);
        ClientPlayNetworking.send(NetworkChannels.SET_ENCHANTMENT_LEVEL, buf);
    }

    public static void recieveSyncConfig(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
        final String identifier = buf.readString();
        final JsonObject json = JsonParser.parseString(buf.readString()).getAsJsonObject();
        client.execute(() -> {
            boolean check = ConfigSyncing.deserializeConfig(identifier, json);
            if (!check) {
                ToolLeveling.LOGGER.error("Config " + identifier + " could not be loaded");
                throw new RuntimeException("Config " + identifier + " could not be loaded");
            } else {
                ToolLeveling.LOGGER.info("Config " + identifier + " recieved and loaded.");
            }
        });
    }

}
