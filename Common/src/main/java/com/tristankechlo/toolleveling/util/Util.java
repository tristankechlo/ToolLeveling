package com.tristankechlo.toolleveling.util;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantmentTableBlock;

import java.util.function.Function;

import static com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity.BOOK_SLOTS;

public final class Util {

    private static boolean canUpgradeProcessBegin(Function<Integer, ItemStack> f) {
        int bookCount = 0;
        for (int i : BOOK_SLOTS) {
            if (!f.apply(i).isEmpty()) {
                bookCount++;
            }
        }
        boolean enoughBooks = bookCount >= ToolLevelingConfig.INSTANCE.requiredBooks(); // the minimum number of books is reached
        boolean upgradeSlotNotEmpty = !f.apply(0).isEmpty(); // the upgrade slot is not empty

        return enoughBooks && upgradeSlotNotEmpty;
    }

    public static boolean canUpgradeProcessBegin(AbstractContainerMenu menu) {
        return canUpgradeProcessBegin((i) -> menu.slots.get(i).getItem());
    }

    public static boolean canUpgradeProcessBegin(Container menu) {
        return canUpgradeProcessBegin(menu::getItem);
    }

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
        int maxPossibleCount = ToolLevelingConfig.INSTANCE.requiredBookshelves();
        count = Math.min(count, maxPossibleCount); // limit the count to the max possible count
        float fullPercent = (float) count / (float) maxPossibleCount;
        float minPercentage = ToolLevelingConfig.INSTANCE.minSuccessChance() * 0.01F;
        float maxPercentage = ToolLevelingConfig.INSTANCE.maxSuccessChance() * 0.01F;
        return minPercentage + ((maxPercentage - minPercentage) * fullPercent);
    }

    public static int getIterations(Container menu) {
        int count = 1;
        for (int i : ToolLevelingTableBlockEntity.BONUS_SLOTS) {
            if (Predicates.BONUS_ITEM_ITERATIONS.test(menu.getItem(i))) {
                count++;
            }
        }
        return count;
    }

    public static int getEnchantmentStrength(Container menu) {
        int count = 1;
        for (int i : ToolLevelingTableBlockEntity.BONUS_SLOTS) {
            if (Predicates.BONUS_ITEM_STRENGTH.test(menu.getItem(i))) {
                count++;
            }
        }
        return count;
    }

}
