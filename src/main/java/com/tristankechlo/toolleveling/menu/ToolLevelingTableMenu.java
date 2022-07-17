package com.tristankechlo.toolleveling.menu;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.menu.slot.EquipmentSlots;
import com.tristankechlo.toolleveling.menu.slot.OffhandSlot;
import com.tristankechlo.toolleveling.menu.slot.PaymentSlot;
import com.tristankechlo.toolleveling.menu.slot.UpgradeSlot;
import com.tristankechlo.toolleveling.utils.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ToolLevelingTableMenu extends AbstractContainerMenu {

    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final int[][] EQUIPMENT_SLOT_POINTS = new int[][]{{197, 136}, {197, 154}, {215, 136}, {215, 154}};
    private final Container table;
    private final Level level;
    private final BlockPos pos;

    public ToolLevelingTableMenu(int id, Inventory playerInv, FriendlyByteBuf extraData) {
        this(id, playerInv, new SimpleContainer(ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS), extraData.readBlockPos());
    }

    public ToolLevelingTableMenu(int id, Inventory playerInv, Container container, BlockPos pos) {
        super(ModRegistry.TLT_CONTAINER.get(), id);
        this.table = container;
        this.level = playerInv.player.level;
        this.pos = pos;

        this.addSlot(new UpgradeSlot(container, 0, 44, 22));
        // payment slots
        int startX = 8;
        int startY = 68;
        int slotSizePlus2 = 18;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 5; column++) {
                this.addSlot(new PaymentSlot(container, 1 + (row * 5) + column, startX + (column * slotSizePlus2), startY + (row * slotSizePlus2)));
            }
        }
        // main inventory
        startY = 136;
        startX = 17;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInv, 9 + (row * 9) + column, startX + (column * slotSizePlus2), startY + (row * slotSizePlus2)));
            }
        }
        // hotbar
        startY = 194;
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInv, column, startX + (column * slotSizePlus2), startY));
        }
        // armor
        for (int i = 0; i < 4; i++) {
            final EquipmentSlot equipmentSlotType = VALID_EQUIPMENT_SLOTS[i];
            final int[] p = EQUIPMENT_SLOT_POINTS[i];
            this.addSlot(new EquipmentSlots(playerInv, p[0], p[1], equipmentSlotType, playerInv.player));
        }
        // offhand
        this.addSlot(new OffhandSlot(playerInv, 206, 172));
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return table.stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        int slotCount = ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS;
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index >= 0 && index < slotCount) {
                if (!this.moveItemStackTo(itemstack1, slotCount, 36 + slotCount, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (this.slots.get(1).mayPlace(itemstack1)) {
                if (!this.moveItemStackTo(itemstack1, 1, slotCount, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (this.slots.get(0).hasItem() || !this.slots.get(0).mayPlace(itemstack1)) {
                    return ItemStack.EMPTY;
                }
                ItemStack itemstack2 = itemstack1.copy();
                itemstack2.setCount(1);
                itemstack1.shrink(1);
                this.slots.get(0).set(itemstack2);
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(playerIn, itemstack1);
        }

        return itemstack;
    }

    public long getContainerWorth() {
        long worth = 0;
        for (int i = 1; i < ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS; i++) {
            ItemStack stack = table.getItem(i);
            worth += Utils.getStackWorth(stack);
        }
        return worth;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public long getBonusPoints() {
        BlockEntity entity = level.getBlockEntity(pos);
        if (entity instanceof ToolLevelingTableBlockEntity) {
            return ((ToolLevelingTableBlockEntity) entity).bonusPoints;
        }
        return 0;
    }

}
