package com.tristankechlo.toolleveling.screenhandler;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.screenhandler.slot.EquipmentSlots;
import com.tristankechlo.toolleveling.screenhandler.slot.OffhandSlot;
import com.tristankechlo.toolleveling.screenhandler.slot.PaymentSlot;
import com.tristankechlo.toolleveling.screenhandler.slot.UpgradeSlot;
import com.tristankechlo.toolleveling.utils.Utils;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ToolLevelingTableScreenhandler extends ScreenHandler {

	private final Inventory inventory;
	private static final EquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EquipmentSlot[] { EquipmentSlot.HEAD,
			EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET };
	private static final int[][] EQUIPMENT_SLOT_POINTS = new int[][] { { 197, 136 }, { 197, 154 }, { 215, 136 },
			{ 215, 154 } };
	private final BlockPos pos;
	private final World world;

	public ToolLevelingTableScreenhandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
		this(syncId, playerInventory, new SimpleInventory(ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS),
				buf.readBlockPos());
	}

	public ToolLevelingTableScreenhandler(int syncId, PlayerInventory playerInventory, Inventory inventory,
			BlockPos pos) {
		super(ToolLeveling.TLT_SCREEN_HANDLER, syncId);
		checkSize(inventory, ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS);
		this.inventory = inventory;
		inventory.onOpen(playerInventory.player);

		this.world = playerInventory.player.world;
		this.pos = pos;

		// upgrade slot
		this.addSlot(new UpgradeSlot(inventory, 0, 44, 22));

		// payment slots
		int startX = 8;
		int startY = 68;
		int slotSizePlus2 = 18;
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 5; column++) {
				this.addSlot(new PaymentSlot(inventory, 1 + (row * 5) + column, startX + (column * slotSizePlus2),
						startY + (row * slotSizePlus2)));
			}
		}

		// main inventory
		startY = 136;
		startX = 17;
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * slotSizePlus2),
						startY + (row * slotSizePlus2)));
			}
		}

		// hotbar
		startY = 194;
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(playerInventory, column, startX + (column * slotSizePlus2), startY));
		}

		// armor
		for (int i = 0; i < 4; i++) {
			final EquipmentSlot equipmentSlotType = VALID_EQUIPMENT_SLOTS[i];
			final int[] p = EQUIPMENT_SLOT_POINTS[i];
			this.addSlot(new EquipmentSlots(playerInventory, p[0], p[1], equipmentSlotType));
		}
		// offhand
		this.addSlot(new OffhandSlot(playerInventory, 206, 172));
	}

	@Override
	public boolean canUse(PlayerEntity player) {
		return this.inventory.canPlayerUse(player);
	}

	@Override
	public ItemStack transferSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		int slotCount = ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS;
		if (slot != null && slot.hasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index >= 0 && index < slotCount) {
				if (!this.insertItem(itemstack1, slotCount, 36 + slotCount, true)) {
					return ItemStack.EMPTY;
				}
			} else if (this.slots.get(1).canInsert(itemstack1)) {
				if (!this.insertItem(itemstack1, 1, slotCount, false)) {
					return ItemStack.EMPTY;
				}
			} else {
				if (this.slots.get(0).hasStack() || !this.slots.get(0).canInsert(itemstack1)) {
					return ItemStack.EMPTY;
				}
				ItemStack itemstack2 = itemstack1.copy();
				itemstack2.setCount(1);
				itemstack1.decrement(1);
				this.slots.get(0).setStack(itemstack2);
			}

			if (itemstack1.isEmpty()) {
				slot.setStack(ItemStack.EMPTY);
			} else {
				slot.markDirty();
			}
			if (itemstack1.getCount() == itemstack.getCount()) {
				return ItemStack.EMPTY;
			}
			slot.onTakeItem(playerIn, itemstack1);
		}
		return itemstack;
	}

	public long getContainerWorth() {
		long worth = 0;
		for (int i = 1; i < ToolLevelingTableBlockEntity.NUMBER_OF_SLOTS; i++) {
			ItemStack stack = inventory.getStack(i);
			worth += Utils.getStackWorth(stack);
		}
		return worth;
	}

	public BlockPos getPos() {
		return pos;
	}

	public long getBonusPoints() {
		BlockEntity entity = world.getBlockEntity(pos);
		if (entity instanceof ToolLevelingTableBlockEntity) {
			return ((ToolLevelingTableBlockEntity) entity).bonusPoints;
		}
		return 0;
	}

}
