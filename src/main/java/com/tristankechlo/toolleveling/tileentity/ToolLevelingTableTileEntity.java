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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ToolLevelingTableTileEntity extends TileEntity implements INameable, IItemHandler, INamedContainerProvider {

	private ITextComponent customname = new TranslationTextComponent("container."+ToolLeveling.MOD_ID+".tool_leveling_table");
	private ItemStackHandler inventory = new ItemStackHandler(1) {
		protected void onContentsChanged(int slot) {
			markDirty();
		};
	};
	
	public ToolLevelingTableTileEntity() {
		super(ModTileEntities.TOOL_LEVELING_TABLE.get());
	}

    @Override
    public void func_230337_a_(BlockState state, CompoundNBT tag) {
        super.func_230337_a_(state, tag);
        if (tag.contains("CustomName", 8)) {
            this.customname = ITextComponent.Serializer.func_240643_a_(tag.getString("CustomName"));
        }
        if(tag.contains("inv")) {
        	CompoundNBT inv = tag.getCompound("inv");
        	this.inventory.deserializeNBT(inv);
        	
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
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if(slot == 0) {
			return inventory.getStackInSlot(0);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
		if(slot == 0 && stack.isEnchanted() && this.inventory.getStackInSlot(0) == ItemStack.EMPTY) {
			final ItemStack accept = stack.split(1);
			this.inventory.insertItem(0, accept, simulate);
			return stack;
		} else {
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
		}
		return ItemStack.EMPTY;
	}

	@Override
	public int getSlotLimit(int slot) {
		return 1;
	}

	@Override
	public boolean isItemValid(int slot, ItemStack stack) {
		return stack.isEnchanted();
	}
	
	public IItemHandler getInventory() {
		return this.inventory;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity playerEtity) {
		return new ToolLevelingTableContainer(windowId, playerInv, this);
	}
    
}
