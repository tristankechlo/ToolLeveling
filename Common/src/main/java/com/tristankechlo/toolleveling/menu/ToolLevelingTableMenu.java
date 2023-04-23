package com.tristankechlo.toolleveling.menu;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.menu.slot.BookSlot;
import com.tristankechlo.toolleveling.menu.slot.PaymentSlot;
import com.tristankechlo.toolleveling.menu.slot.UpgradeSlot;
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

public class ToolLevelingTableMenu extends AbstractContainerMenu {

    private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    private static final int[][] EQUIPMENT_SLOT_POINTS = new int[][]{{197, 136}, {197, 154}, {215, 136}, {215, 154}};
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
        this.addSlot(new UpgradeSlot(container, 0, 53, 53));

        // main inventory
        int startX = 8;
        int startY = 125;
        int slotSizePlus2 = 18;
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInv, 9 + (row * 9) + column, startX + (column * slotSizePlus2), startY + (row * slotSizePlus2)));
            }
        }
        // hotbar
        startY = 183;
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInv, column, startX + (column * slotSizePlus2), startY));
        }

        // payment slots
        this.addSlot(new PaymentSlot(container, 1, 21, 54));
        this.addSlot(new PaymentSlot(container, 2, 71, 23));
        this.addSlot(new PaymentSlot(container, 3, 71, 85));

        // enchanted book slots
        this.addSlot(new BookSlot(container, 4, 37, 23));
        this.addSlot(new BookSlot(container, 5, 87, 54));
        this.addSlot(new BookSlot(container, 6, 37, 85));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        int slotCount = ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS;
        if (!slot.hasItem()) {
            return itemstack;
        }
        return null;
    }

    @Override
    public boolean stillValid(Player player) {
        return this.table.stillValid(player);
    }

    public BlockPos getPos() {
        return this.pos;
    }

}
