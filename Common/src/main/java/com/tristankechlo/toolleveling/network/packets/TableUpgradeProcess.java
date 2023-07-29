package com.tristankechlo.toolleveling.network.packets;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;

public record TableUpgradeProcess(BlockPos pos) {

    public static void encode(FriendlyByteBuf buf, BlockPos pos) {
        buf.writeBlockPos(pos);
    }

    public static TableUpgradeProcess decode(FriendlyByteBuf buf) {
        return new TableUpgradeProcess(buf.readBlockPos());
    }

    public static void handle(TableUpgradeProcess msg, ServerLevel level) {
        if (level == null || !level.hasChunkAt(msg.pos)) {
            return;
        }
        BlockEntity entity = level.getBlockEntity(msg.pos);
        if ((entity instanceof ToolLevelingTableBlockEntity table)) {
            if (!Util.canUpgradeProcessBegin(table)) {
                ToolLeveling.LOGGER.warn("TableUpgradeProcess: can not start upgrade process");
                return;
            }

            // determine the enchantments to add
            int cycles = Util.getCycles(table);
            var possibleEnchantments = table.getEnchantments();
            float successChance = Util.getSuccessChance(level, msg.pos);
            Map<Enchantment, Integer> enchantmentsToAdd = new HashMap<>();

            for (int i = 0; i < cycles; i++) {
                float nextFloat = level.getRandom().nextFloat();
                if (nextFloat > successChance) {
                    ToolLeveling.LOGGER.info("TableUpgradeProcess failed {} {}", successChance, nextFloat);
                    continue;
                }
                var o = WeightedRandom.getRandomItem(level.getRandom(), possibleEnchantments);
                if (o.isEmpty()) {
                    ToolLeveling.LOGGER.warn("TableUpgradeProcess: no enchantment selected");
                    continue;
                }
                Enchantment e = o.get().getData();
                int enchantmentLevel = Util.getLevels(table);
                if (enchantmentsToAdd.containsKey(e)) {
                    enchantmentLevel += enchantmentsToAdd.get(e);
                }
                enchantmentsToAdd.put(e, enchantmentLevel);
            }

            // add the enchantments to the item
            ItemStack tool = table.getStackToEnchant();
            var oldEnchantments = EnchantmentHelper.getEnchantments(tool);
            for (var entry : enchantmentsToAdd.entrySet()) {
                oldEnchantments.merge(entry.getKey(), entry.getValue(), Integer::sum);
                ToolLeveling.LOGGER.info("TableUpgradeProcess: added {} levels to enchantment {}", entry.getValue(), entry.getKey().getDescriptionId());
            }
            EnchantmentHelper.setEnchantments(oldEnchantments, tool);

            // clear all slots except the first one
            for (int i = 1; i < ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS; i++) {
                table.setItem(i, ItemStack.EMPTY);
            }
        }
    }

}
