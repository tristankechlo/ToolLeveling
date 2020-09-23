package com.tristankechlo.toolleveling.tileentity;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.container.ToolLevelingTableContainer;
import com.tristankechlo.toolleveling.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ToolLevelingTableTileEntity extends TileEntity implements INameable, IItemHandler, INamedContainerProvider {

	private ITextComponent customname = new TranslationTextComponent("container."+ToolLeveling.MOD_ID+".tool_leveling_table");
	public ItemStackHandler inventory = new ItemStackHandler(2) {
		protected void onContentsChanged(int slot) {
			markDirty();
		};
	};
	
	public ToolLevelingTableTileEntity() {
		super(ModTileEntities.TOOL_LEVELING_TABLE.get());
	}
	
    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        if (tag.contains("CustomName", 8)) {
            this.customname = ITextComponent.Serializer.func_240643_a_(tag.getString("CustomName"));
        }
        if(tag.contains("inv")) {
        	CompoundNBT inv = tag.getCompound("inv");
        	this.inventory.deserializeNBT(inv);;
        	
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        if (this.hasCustomName()) {
            tag.putString("CustomName", ITextComponent.Serializer.toJson(this.customname));
        }
        if(this.inventory.getStackInSlot(0) != ItemStack.EMPTY) {
            tag.put("inv", this.inventory.serializeNBT());
        }
		return tag;
    }

	@Override
	public ITextComponent getName() {
		return this.customname;
	}
	
	@Override
	public ITextComponent getCustomName() {
	      return this.customname;
	}
	
	@Override
	public ITextComponent getDisplayName() {
	      return this.customname;
	}

	@Override
	public int getSlots() {
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot == 0 || slot == 1) {
			return inventory.getStackInSlot(slot);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(slot == 0 && stack.isEnchanted() && this.inventory.getStackInSlot(0) == ItemStack.EMPTY) {
			final ItemStack accepted_stack = stack.split(1);
			this.inventory.insertItem(0, accepted_stack, simulate);
			return stack;
		} else if (slot == 1 && (stack.getItem() == Items.DIAMOND) && (this.inventory.getStackInSlot(1).getCount() < Items.DIAMOND.getMaxStackSize())) {
			final ItemStack accepted_stack = stack.split(Items.DIAMOND.getMaxStackSize() - this.inventory.getStackInSlot(1).getCount());
			this.inventory.insertItem(1, accepted_stack, simulate);
			return stack;
		}else {
			return stack;
		}
	}

	@Override
	public ItemStack extractItem(int slot, int amount, boolean simulate) {
		if(slot == 0 && !(this.inventory.getStackInSlot(0) == ItemStack.EMPTY)) {
			if(this.inventory.getStackInSlot(0).getCount() >= amount) {
				ItemStack stack = this.inventory.extractItem(0, amount, simulate);
				return stack;
			}
		}if(slot == 1 && !(this.inventory.getStackInSlot(1) == ItemStack.EMPTY)) {
			if(this.inventory.getStackInSlot(1).getCount() >= amount) {
				ItemStack stack = this.inventory.extractItem(0, amount, simulate);
				return stack;
			}
		}
		return ItemStack.EMPTY;
	}

	@SuppressWarnings("deprecation")
	@Override
	public int getSlotLimit(int slot) {
		if(slot == 0) {
			return 1;
		}
		return Items.DIAMOND.getMaxStackSize();
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		if(slot == 0) {
			return stack.isEnchanted();
		} else if(slot == 1) {
			return stack.getItem() == Items.DIAMOND;
		}
		return false;
	}
	
	public IItemHandler getInventory() {
		return this.inventory;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity playerEntity) {
		return new ToolLevelingTableContainer(windowId, playerInv, this);
	}
		
}
