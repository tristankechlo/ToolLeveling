package com.tristankechlo.toolleveling.network.packets;

import java.util.Map;
import java.util.function.Supplier;

import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;
import com.tristankechlo.toolleveling.utils.Utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SetEnchantmentToolLevelingTable {

	private final BlockPos pos;
	private final Enchantment enchantment;
	private final int level;

	public SetEnchantmentToolLevelingTable(BlockPos pos, Enchantment enchantment, int level) {
		this.pos = pos;
		this.enchantment = enchantment;
		this.level = level;
	}

	public static void encode(SetEnchantmentToolLevelingTable msg, PacketBuffer buffer) {
		buffer.writeBlockPos(msg.pos);
		buffer.writeResourceLocation(msg.enchantment.getRegistryName());
		buffer.writeInt(msg.level);
	}

	public static SetEnchantmentToolLevelingTable decode(PacketBuffer buffer) {
		BlockPos pos = buffer.readBlockPos();
		Enchantment enchantment = ForgeRegistries.ENCHANTMENTS.getValue(buffer.readResourceLocation());
		int level = buffer.readInt();
		return new SetEnchantmentToolLevelingTable(pos, enchantment, level);
	}

	@SuppressWarnings("deprecation")
	public static void handle(SetEnchantmentToolLevelingTable msg, Supplier<NetworkEvent.Context> context) {

		context.get().enqueueWork(() -> {

			ServerPlayerEntity player = context.get().getSender();
			if (player == null) {
				return;
			}
			ServerWorld world = player.getServerWorld();
			if (world == null || !world.isBlockLoaded(msg.pos)) {
				return;
			}
//			ToolLeveling.LOGGER.debug(msg.pos);
			TileEntity entity = world.getTileEntity(msg.pos);
			if (entity != null && (entity instanceof ToolLevelingTableTileEntity)) {

				ToolLevelingTableTileEntity table = (ToolLevelingTableTileEntity) entity;
				ItemStack enchantedItem = table.getStackToEnchant().copy();
				Map<Enchantment, Integer> enchantmentsMap = EnchantmentHelper.getEnchantments(enchantedItem);

				if (enchantmentsMap.containsKey(msg.enchantment)) {
					int upgradeCost = Utils.getEnchantmentUpgradeCost(msg.level);
					boolean result = table.decreaseInventoryWorth(upgradeCost);
					if (result) {
						enchantmentsMap.put(msg.enchantment, msg.level);
						EnchantmentHelper.setEnchantments(enchantmentsMap, enchantedItem);
						table.chestContents.setInventorySlotContents(0, enchantedItem);
						table.markDirty();
					}
				}
			}
		});
		context.get().setPacketHandled(true);
	}

}
