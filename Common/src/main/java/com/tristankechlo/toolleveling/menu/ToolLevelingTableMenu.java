package com.tristankechlo.toolleveling.menu;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.menu.slot.PredicateSlot;
import com.tristankechlo.toolleveling.menu.slot.UpgradeSlot;
import com.tristankechlo.toolleveling.util.Predicates;
import com.tristankechlo.toolleveling.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.Tuple;
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
import java.util.stream.Collectors;

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
        this.addSlot(new UpgradeSlot(container, 0, 80, 49));

        // payment slots
        this.addSlot(new PredicateSlot(container, 1, 49, 50, Predicates.PAYMENT));
        this.addSlot(new PredicateSlot(container, 2, 97, 80, Predicates.PAYMENT));
        this.addSlot(new PredicateSlot(container, 3, 97, 18, Predicates.PAYMENT));

        // enchanted book slots
        this.addSlot(new PredicateSlot(container, 4, 63, 18, Predicates.BOOK));
        this.addSlot(new PredicateSlot(container, 5, 63, 80, Predicates.BOOK));
        this.addSlot(new PredicateSlot(container, 6, 111, 49, Predicates.BOOK));

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
            if (!this.moveItemStackTo(itemstack1, 4, slotCount, false)) {
                return ItemStack.EMPTY;
            }
        } else { // try placing into upgrade slot
            if (this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(itemstack1)) {
                return ItemStack.EMPTY;
            }
            ItemStack itemstack2 = itemstack1.copy();
            itemstack2.setCount(1);
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

    public List<Tuple<Enchantment, Float>> getPercentages() {
        int totalWeight = 0;
        Map<Enchantment, Integer> enchantments = new HashMap<>();
        for (int i = 4; i < 7; i++) {
            ItemStack stack = this.slots.get(i).getItem();
            Map<Enchantment, Integer> m = EnchantmentHelper.getEnchantments(stack);
            for (Map.Entry<Enchantment, Integer> entry : m.entrySet()) {
                enchantments.merge(entry.getKey(), entry.getValue(), Integer::sum);
                totalWeight += entry.getValue();
            }
        }
        int finalTotalWeight = totalWeight;
        return enchantments.entrySet().stream().map(e -> new Tuple<>(e.getKey(), (float) e.getValue() / (float) finalTotalWeight)).collect(Collectors.toList());
    }

}
