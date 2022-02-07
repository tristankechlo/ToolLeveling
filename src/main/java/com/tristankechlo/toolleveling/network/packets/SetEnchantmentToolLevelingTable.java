package com.tristankechlo.toolleveling.network.packets;

import java.util.Map;
import java.util.function.Supplier;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.utils.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public final class SetEnchantmentToolLevelingTable {

	private final BlockPos pos;
	private final Enchantment enchantment;
	private final int level;

	public SetEnchantmentToolLevelingTable(BlockPos pos, Enchantment enchantment, int level) {
		this.pos = pos;
		this.enchantment = enchantment;
		this.level = level;
	}

	public static void encode(SetEnchantmentToolLevelingTable msg, FriendlyByteBuf buffer) {
		buffer.writeBlockPos(msg.pos);
		buffer.writeResourceLocation(msg.enchantment.getRegistryName());
		buffer.writeInt(msg.level);
	}

	public static SetEnchantmentToolLevelingTable decode(FriendlyByteBuf buffer) {
		BlockPos pos = buffer.readBlockPos();
		Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
		int level = buffer.readInt();
		return new SetEnchantmentToolLevelingTable(pos, enchantment, level);
	}

	public static void handle(SetEnchantmentToolLevelingTable msg, Supplier<NetworkEvent.Context> context) {

		context.get().enqueueWork(() -> {

			ServerPlayer player = context.get().getSender();
			if (player == null) {
				ToolLeveling.LOGGER.warn("Error while handling the request. Invalid sender.");
				return;
			}
			ServerLevel level = player.getLevel();
			if (level == null || !level.isLoaded(msg.pos) || level.isClientSide) {
				ToolLeveling.LOGGER.warn("Error while handling the request. Invalid level.");
				return;
			}
			BlockEntity entity = level.getBlockEntity(msg.pos);
			if (entity != null && (entity instanceof ToolLevelingTableBlockEntity)) {

				ToolLevelingTableBlockEntity table = (ToolLevelingTableBlockEntity) entity;
				ItemStack enchantedItem = table.getStackToEnchant().copy();
				Map<Enchantment, Integer> enchantmentsMap = EnchantmentHelper.getEnchantments(enchantedItem);

				if (enchantmentsMap.containsKey(msg.enchantment)) {
					long upgradeCost = Utils.getEnchantmentUpgradeCost(msg.level);
					boolean upgradeSuccess = false;
					if(Utils.freeCreativeUpgrades(player)) {
						upgradeSuccess = true;
					} else {
						upgradeSuccess = table.decreaseInventoryWorth(upgradeCost);
					}
					if (upgradeSuccess) {
						enchantmentsMap.put(msg.enchantment, msg.level);
						EnchantmentHelper.setEnchantments(enchantmentsMap, enchantedItem);
						table.setItem(0, enchantedItem);
						table.setChanged();
					}
				}
			}
		});
		context.get().setPacketHandled(true);
	}

}
