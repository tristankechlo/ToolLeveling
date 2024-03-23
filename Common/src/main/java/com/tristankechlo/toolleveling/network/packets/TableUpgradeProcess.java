package com.tristankechlo.toolleveling.network.packets;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;

public record TableUpgradeProcess(BlockPos pos) {

    public static final ResourceLocation CHANNEL_ID = new ResourceLocation(ToolLeveling.MOD_ID, "tool_leveling_table");

    // forge specific method for packet encoding
    public static void encode(TableUpgradeProcess msg, FriendlyByteBuf buf) {
        encode(buf, msg.pos);
    }

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

            float iterations = Util.getIterations(table); // how often the process will be repeated
            int trueIterations = (int) Math.floor(iterations);
            if (iterations > trueIterations && level.getRandom().nextFloat() < iterations - trueIterations) {
                trueIterations++;
            }
            float minStrength = Util.getEnchantmentMinStrength(table); // the minimum level of the enchantments
            float strength = Util.getEnchantmentStrength(table); // the maximum level of the enchantments
            var possibleEnchantments = table.getEnchantments(); // the possible enchantments, with their weight
            float successChance = Util.getSuccessChance(level, msg.pos); // the chance of a success in each iteration
            ItemStack tool = table.getStackToEnchant(); // the tool to enchant
            var oldEnchantments = EnchantmentHelper.getEnchantments(tool); // the enchantments the tool already has
            Map<Enchantment, Integer> enchantmentsToAdd = new HashMap<>(); // the enchantments to add

            // clear all slots except the first one
            for (int i = 1; i < ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS; i++) {
                table.setItem(i, ItemStack.EMPTY);
            }

            // calculate the enchantments to add
            for (int i = 0; i < trueIterations; i++) {
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
                // strength wins if it is lower than minStrengtg
                float enchantmentLevel = Math.min(minStrength, strength);
                if (strength > minStrength) {
                    enchantmentLevel += level.getRandom().nextFloat() * (strength - minStrength);
                }
                int trueEnchantmentLevel = (int) Math.floor(enchantmentLevel);
                if (enchantmentLevel > trueEnchantmentLevel && level.getRandom().nextFloat() < enchantmentLevel - trueEnchantmentLevel) {
                    trueEnchantmentLevel++;
                }
                // if minimum strength is below 1, the resulting level can be too - don't add or subtract in that case
                if (trueEnchantmentLevel > 0) {
                    if (enchantmentsToAdd.containsKey(e)) { // if the enchantment is already in the map, sum up the levels
                        trueEnchantmentLevel += enchantmentsToAdd.get(e);
                    }
                    enchantmentsToAdd.put(e, trueEnchantmentLevel);
                }
            }

            // add the enchantments to the item
            for (var entry : enchantmentsToAdd.entrySet()) {
                oldEnchantments.merge(entry.getKey(), entry.getValue(), (l, r) -> Math.min(l + r, Short.MAX_VALUE));
            }
            EnchantmentHelper.setEnchantments(oldEnchantments, tool);
            table.setChanged();
        }
    }

}
