package com.tristankechlo.toolleveling.container;

import java.util.Objects;

import com.tristankechlo.toolleveling.init.ModBlocks;
import com.tristankechlo.toolleveling.init.ModContainers;
import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.SlotItemHandler;

public class ToolLevelingTableContainer extends Container {

	private final IWorldPosCallable worldPos;
	private ToolLevelingTableTileEntity entity;
	private BlockPos pos;

	public ToolLevelingTableContainer(final int windowId, final PlayerInventory playerInv, final PacketBuffer data) {
		this(windowId, playerInv, getTileEntity(playerInv, data));
	}
	
	private static ToolLevelingTableTileEntity getTileEntity(final PlayerInventory playerInv, final PacketBuffer data) {
		Objects.requireNonNull(playerInv, "playerInv cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInv.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof ToolLevelingTableTileEntity) {
			return (ToolLevelingTableTileEntity) tileAtPos;
		}
		throw new IllegalStateException("TileEntity is not correct " + tileAtPos);
	}
	
	public ToolLevelingTableContainer(final int windowId, final PlayerInventory playerInv, final ToolLevelingTableTileEntity entity) {
	    super(ModContainers.TOOL_LEVELING_TABLE.get(), windowId);
		this.worldPos = IWorldPosCallable.of(entity.getWorld(), entity.getPos());
		this.entity = entity;
		this.pos = entity.getPos();

		this.addSlot(new SlotItemHandler(entity.getInventory(), 0, 15, 23) {
			@Override
			public boolean isItemValid(ItemStack stack) {
				return stack.isEnchanted();
			}
			@Override
			public int getSlotStackLimit() {
				return 1;
			}
			@Override
			public void onSlotChanged() {
				entity.markDirty();
			}
		});
		
		// Main Inventory
		int startX = 10;
		int startY = 130;
		int slotSizePlus2 = 18;
		for (int row = 0; row < 3; row++) {
			for (int column = 0; column < 9; column++) {
				this.addSlot(new Slot(playerInv, 9 + (row * 9) + column, startX + (column * slotSizePlus2),
						startY + (row * slotSizePlus2)));
			}
		}

		// Hotbar
		for (int column = 0; column < 9; column++) {
			this.addSlot(new Slot(playerInv, column, startX + (column * slotSizePlus2), 188));
		}
	}
	
	@Override
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(this.worldPos, playerIn, ModBlocks.TOOL_LEVELING_TABLE.get());
    }
	
	

	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
	      ItemStack itemstack = ItemStack.EMPTY;
	      Slot slot = this.inventorySlots.get(index);
	      if (slot != null && slot.getHasStack()) {
	         ItemStack itemstack1 = slot.getStack();
	         itemstack = itemstack1.copy();
	         if (index == 0) {
	            if (!this.mergeItemStack(itemstack1, 2, 37, true)) {
	               return ItemStack.EMPTY;
	            }
	         } else {
	            if (this.inventorySlots.get(0).getHasStack() || !this.inventorySlots.get(0).isItemValid(itemstack1)) {
	               return ItemStack.EMPTY;
	            }

	            ItemStack itemstack2 = itemstack1.copy();
	            itemstack2.setCount(1);
	            itemstack1.shrink(1);
	            this.inventorySlots.get(0).putStack(itemstack2);
	         }

	         if (itemstack1.isEmpty()) {
	            slot.putStack(ItemStack.EMPTY);
	         } else {
	            slot.onSlotChanged();
	         }

	         if (itemstack1.getCount() == itemstack.getCount()) {
	            return ItemStack.EMPTY;
	         }

	         slot.onTake(playerIn, itemstack1);
	      }

	      return itemstack;
	}
	
	public BlockPos getEntityPos() {
		return this.pos;
	}
	
	public ToolLevelingTableTileEntity getEntity() {
		return this.entity;
	}

}
