package com.tristankechlo.toolleveling.network;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.utils.Names.NetworkChannels;
import com.tristankechlo.toolleveling.utils.Utils;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public final class ServerNetworkHandler {

	public static void sendOpenItemValues(ServerPlayerEntity player) {
		ServerPlayNetworking.send(player, NetworkChannels.OPEN_ITEMVALUES, PacketByteBufs.empty());
	}

	@SuppressWarnings("deprecation")
	public static void recieveSetEnchantmentLevel(MinecraftServer server, ServerPlayerEntity player,
			ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender) {
		final BlockPos pos = buf.readBlockPos();
		final Enchantment enchantment = Registry.ENCHANTMENT.get(buf.readIdentifier());
		final int level = buf.readInt();
		server.execute(() -> {
	
			if (player == null) {
				ToolLeveling.LOGGER.warn("Error while handling the request. Invalid sender.");
				return;
			}
			ServerWorld world = player.getWorld();
			if (world == null || !world.isChunkLoaded(pos) || world.isClient) {
				ToolLeveling.LOGGER.warn("Error while handling the request. Invalid level.");
			}
	
			BlockEntity entity = world.getBlockEntity(pos);
			if (entity != null && (entity instanceof ToolLevelingTableBlockEntity)) {
	
				ToolLevelingTableBlockEntity table = (ToolLevelingTableBlockEntity) entity;
				ItemStack enchantedItem = table.getStackToEnchant().copy();
				Map<Enchantment, Integer> enchantmentsMap = EnchantmentHelper.get(enchantedItem);
	
				if (enchantmentsMap.containsKey(enchantment)) {
					long upgradeCost = Utils.getEnchantmentUpgradeCost(level);
					boolean result = table.decreaseInventoryWorth(upgradeCost);
					if (result) {
						enchantmentsMap.put(enchantment, level);
						EnchantmentHelper.set(enchantmentsMap, enchantedItem);
						table.setStack(0, enchantedItem);
						table.markDirty();
					}
				}
			}
		});
	}

	public static void sendSyncConfig(ServerPlayerEntity player, String identifier, JsonObject json) {
		PacketByteBuf buf = PacketByteBufs.create();
		buf.writeString(identifier);
		buf.writeString(new Gson().toJson(json));
		ServerPlayNetworking.send(player, NetworkChannels.SYNC_CONFIG, buf);
	}

}
