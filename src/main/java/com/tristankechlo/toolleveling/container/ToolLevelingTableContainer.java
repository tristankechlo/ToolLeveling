package com.tristankechlo.toolleveling.container;

import com.tristankechlo.toolleveling.container.slot.EquipmentSlot;
import com.tristankechlo.toolleveling.container.slot.OffhandSlot;
import com.tristankechlo.toolleveling.container.slot.PaymentSlot;
import com.tristankechlo.toolleveling.container.slot.UpgradeSlot;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;
import com.tristankechlo.toolleveling.utils.ChestContents;
import com.tristankechlo.toolleveling.utils.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

public class ToolLevelingTableContainer extends Container {

    private ChestContents chestContents;
    private ToolLevelingTableTileEntity table;
    private static final EquipmentSlotType[] VALID_EQUIPMENT_SLOTS = new EquipmentSlotType[]{EquipmentSlotType.HEAD, EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET};
    private static final int[][] EQUIPMENT_SLOT_POINTS = new int[][]{{197, 136}, {197, 154}, {215, 136}, {215, 154}};

    public static ToolLevelingTableContainer createForServerSide(int id, PlayerInventory playerInventory, ChestContents chestContents, BlockPos pos) {
        return new ToolLevelingTableContainer(id, playerInventory, chestContents, pos);
    }

    public static ToolLevelingTableContainer createForClientSide(int id, PlayerInventory playerInv, PacketBuffer extraData) {
        BlockPos pos = extraData.readBlockPos();
        ChestContents chestContents = ChestContents.createForClientSideContainer(ToolLevelingTableTileEntity.NUMBER_OF_SLOTS);
        return new ToolLevelingTableContainer(id, playerInv, chestContents, pos);
    }

    private ToolLevelingTableContainer(int id, PlayerInventory playerInv, ChestContents chestContents, BlockPos pos) {
        super(ModRegistry.TLT_CONTAINER.get(), id);
        this.chestContents = chestContents;
        this.table = (ToolLevelingTableTileEntity) playerInv.player.level.getBlockEntity(pos);

        this.addSlot(new UpgradeSlot(this.chestContents, 0, 44, 22));
        // payment slots
        int startX = 8;
        int startY = 68;
        int slotSizePlus2 = 18;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 5; column++) {
                this.addSlot(new PaymentSlot(this.chestContents, 1 + (row * 5) + column, startX + (column * slotSizePlus2), startY + (row * slotSizePlus2)));
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
            final EquipmentSlotType equipmentSlotType = VALID_EQUIPMENT_SLOTS[i];
            final int[] p = EQUIPMENT_SLOT_POINTS[i];
            this.addSlot(new EquipmentSlot(playerInv, p[0], p[1], equipmentSlotType, playerInv.player));
        }
        // offhand
        this.addSlot(new OffhandSlot(playerInv, 206, 172));
    }

    @Override
    public void removed(PlayerEntity playerIn) {
        super.removed(playerIn);
    }

    @Override
    public boolean stillValid(PlayerEntity playerIn) {
        return chestContents.stillValid(playerIn);
    }

    @Override
    public ItemStack quickMoveStack(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        int slotCount = ToolLevelingTableTileEntity.NUMBER_OF_SLOTS;
        if (slot != null && slot.hasItem()) {
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
        for (int i = 1; i < chestContents.getContainerSize(); i++) {
            ItemStack stack = chestContents.getItem(i);
            worth += Utils.getStackWorth(stack);
        }
        return worth;
    }

    public BlockPos getPos() {
        return table.getBlockPos();
    }

    public long getBonusPoints() {
        return table.bonusPoints;
    }
}
