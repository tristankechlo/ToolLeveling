package com.tristankechlo.toolleveling.util;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;

public final class Util {

    public static float getSuccessChance(AbstractContainerMenu menu) {
        BlockPos pos = ((ToolLevelingTableMenu) menu).getPos();
        Level level = ((ToolLevelingTableMenu) menu).getLevel();
        return getSuccessChance(level, pos);
    }

    public static float getSuccessChance(Level level, BlockPos tablePos) {
        int count = 0;
        for (BlockPos pos : EnchantmentTableBlock.BOOKSHELF_OFFSETS) {
            if (EnchantmentTableBlock.isValidBookShelf(level, tablePos, pos)) {
                count++;
            }
        }
        int maxPossibleCount = ToolLevelingConfig.requiredBookshelves.get();
        count = Math.min(count, maxPossibleCount); // limit the count to the max possible count
        float fullPercent = (float) count / (float) maxPossibleCount;
        float minPercentage = ToolLevelingConfig.minSuccessChance.get() * 0.01F;
        float maxPercentage = ToolLevelingConfig.maxSuccessChance.get() * 0.01F;
        return minPercentage + ((maxPercentage - minPercentage) * fullPercent);
    }

    public static int getCycles(Container menu) {
        int count = 1;
        for (int i : ToolLevelingTableBlockEntity.BONUS_SLOTS) {
            if (Predicates.ITEM_EXTRA_ENCHANTMENT.test(menu.getItem(i))) {
                count++;
            }
        }
        return count;
    }

    public static int getLevels(Container menu) {
        int count = 1;
        for (int i : ToolLevelingTableBlockEntity.BONUS_SLOTS) {
            if (Predicates.ITEM_HIGHER_LEVEL.test(menu.getItem(i))) {
                count++;
            }
        }
        return count;
    }

}
