package com.tristankechlo.toolleveling.menu;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.menu.slot.PredicateSlot;
import com.tristankechlo.toolleveling.menu.slot.UpgradeSlot;
import com.tristankechlo.toolleveling.util.Predicates;
import com.tristankechlo.toolleveling.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToolLevelingTableMenu extends AbstractContainerMenu {

    private final Container table;
    private final Level level;
    private final BlockPos pos;


    public ToolLevelingTableMenu(int i, Inventory inventory, FriendlyByteBuf friendlyByteBuf) {
        this(i, inventory, new SimpleContainer(ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS), friendlyByteBuf.readBlockPos());
    }

    public ToolLevelingTableMenu(int id, Inventory playerInv, Container container, BlockPos pos) {
        super(ToolLeveling.TLT_MENU.get(), id);
        this.table = container;
        this.level = playerInv.player.level;
        this.pos = pos;

        // upgrade slot
        this.addSlot(new UpgradeSlot(container, 0, 56, 49));

        // payment slots
        this.addSlot(new PredicateSlot(container, 1, 25, 50, Predicates.PAYMENT));
        this.addSlot(new PredicateSlot(container, 2, 73, 80, Predicates.PAYMENT));
        this.addSlot(new PredicateSlot(container, 3, 73, 18, Predicates.PAYMENT));

        // enchanted book slots
        this.addSlot(new PredicateSlot(container, 4, 39, 18, Predicates.BOOK));
        this.addSlot(new PredicateSlot(container, 5, 39, 80, Predicates.BOOK));
        this.addSlot(new PredicateSlot(container, 6, 87, 49, Predicates.BOOK));

        // bonus slots
        this.addSlot(new PredicateSlot(container, 7, 134, 18, Predicates.BONUS, 1));
        this.addSlot(new PredicateSlot(container, 8, 134, 36, Predicates.BONUS, 1));
        this.addSlot(new PredicateSlot(container, 9, 134, 54, Predicates.BONUS, 1));

        // main inventory
        int startX = 8;
        int startY = 112;
        int slotSizePlus2 = 18;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInv, 9 + (row * 9) + column, startX + (column * slotSizePlus2), startY + (row * slotSizePlus2)));
            }
        }
        // hotbar
        startY += 58;
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInv, column, startX + (column * slotSizePlus2), startY));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(index);
        int slotCount = ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS;

        if (!clickedSlot.hasItem()) { // clicked on an empty slot
            return itemstack;
        }

        ItemStack itemstack1 = clickedSlot.getItem();
        itemstack = itemstack1.copy();

        if (index >= 0 && index < slotCount) { // shift clicked in the table
            if (!this.moveItemStackTo(itemstack1, slotCount, 36 + slotCount, true)) {
                return ItemStack.EMPTY;
            }
        } else if (this.slots.get(1).mayPlace(itemstack1)) { // try placing into book slot
            if (!this.moveItemStackTo(itemstack1, 1, 4, false)) {
                return ItemStack.EMPTY;
            }
        } else if (this.slots.get(4).mayPlace(itemstack1)) { // try placing into payment slot
            if (!this.moveItemStackTo(itemstack1, 4, 7, false)) {
                return ItemStack.EMPTY;
            }
        } else if (this.slots.get(7).mayPlace(itemstack1)) { // try placing into bonus slot
            if (this.slots.get(7).hasItem() && this.slots.get(8).hasItem() && this.slots.get(9).hasItem()) {
                return ItemStack.EMPTY;
            }
            ItemStack itemstack2 = itemstack1.copyWithCount(1);
            itemstack1.shrink(1); // reduce original stack size by 1
            for (int i = 7; i < 10; i++) {
                if (!this.slots.get(i).hasItem()) {
                    this.slots.get(i).set(itemstack2);
                    break;
                }
            }
        } else { // try placing into upgrade slot
            if (this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(itemstack1)) {
                return ItemStack.EMPTY;
            }
            ItemStack itemstack2 = itemstack1.copyWithCount(1);
            itemstack1.shrink(1);
            this.slots.get(0).set(itemstack2);
        }

        if (itemstack1.isEmpty()) {
            clickedSlot.set(ItemStack.EMPTY);
        } else {
            clickedSlot.setChanged();
        }
        if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
        }
        clickedSlot.onTake(player, itemstack1);
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.table.stillValid(player);
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public boolean hasAnyBooks() {
        for (int i = 4; i < 7; i++) {
            if (this.slots.get(i).hasItem()) {
                return true;
            }
        }
        return false;
    }

    public int getCycles() {
        return Util.getCycles(this.table);
    }

    public int getLevels() {
        return Util.getLevels(this.table);
    }

    public List<PercentageHolder> getPercentages() {
        int totalWeight = 0;
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        for (int i = 4; i <= 6; i++) {
            ItemStack stack = this.slots.get(i).getItem();
            var m = EnchantmentHelper.getEnchantments(stack);
            for (Map.Entry<Enchantment, Integer> entry : m.entrySet()) {
                enchantments.merge(entry.getKey(), entry.getValue(), Integer::sum);
                totalWeight += entry.getValue();
            }
        }
        float finalWeight = totalWeight;
        return enchantments.entrySet().stream().map(PercentageHolder::new).map((p) -> p.calcPercentage(finalWeight)).sorted().toList();
    }

    public static class PercentageHolder implements Comparable<PercentageHolder> {

        public final Enchantment enchantment;
        private final int weight;
        public float percentage;

        public PercentageHolder(Map.Entry<Enchantment, Integer> entry) {
            enchantment = entry.getKey();
            weight = entry.getValue();
        }

        public PercentageHolder calcPercentage(float totalWeight) {
            percentage = weight / totalWeight;
            return this;
        }

        @Override
        public int compareTo(PercentageHolder o) {
            return Float.compare(o.percentage, this.percentage);
        }

    }
}
