package com.tristankechlo.toolleveling.network.packets;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public record SetEnchantmentToolLevelingTable(BlockPos pos, Enchantment enchantment, int level) {

    public FriendlyByteBuf writeToBuffer(FriendlyByteBuf buffer) {
        encode(this, buffer);
        return buffer;
    }

    public static void encode(SetEnchantmentToolLevelingTable msg, FriendlyByteBuf buffer) {
        buffer.writeBlockPos(msg.pos);
        ResourceLocation loc = Registry.ENCHANTMENT.getKey(msg.enchantment);
        buffer.writeResourceLocation(loc);
        buffer.writeInt(msg.level);
    }

    public static SetEnchantmentToolLevelingTable decode(FriendlyByteBuf buffer) {
        BlockPos pos = buffer.readBlockPos();
        ResourceLocation loc = buffer.readResourceLocation();
        Enchantment enchantment = Registry.ENCHANTMENT.getOptional(loc).orElseThrow();
        int level = buffer.readInt();
        return new SetEnchantmentToolLevelingTable(pos, enchantment, level);
    }

    public static void handle(SetEnchantmentToolLevelingTable msg, ServerPlayer player, ServerLevel world) {
        if (world == null || !world.hasChunkAt(msg.pos)) {
            ToolLeveling.LOGGER.warn("Received SetEnchantmentToolLevelingTable packet for invalid position: {}", msg.pos);
            return;
        }
        BlockEntity entity = world.getBlockEntity(msg.pos);

        if (entity instanceof ToolLevelingTableBlockEntity table) {
            ItemStack enchantedItem = table.getStackToEnchant().copy();
            Map<Enchantment, Integer> enchantmentsMap = EnchantmentHelper.getEnchantments(enchantedItem);

            if (enchantmentsMap.containsKey(msg.enchantment)) {
                long upgradeCost = Utils.getEnchantmentUpgradeCost(msg.enchantment, msg.level);
                boolean upgradeSuccess = false;
                if (Utils.freeCreativeUpgrades(player)) {
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
    }

}
