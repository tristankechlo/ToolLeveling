package com.tristankechlo.toolleveling.tileentity;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.container.ToolLevelingTableContainer;
import com.tristankechlo.toolleveling.init.ModTileEntities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class ToolLevelingTableTileEntity extends TileEntity implements INamedContainerProvider {

	private ITextComponent customname = new TranslationTextComponent("container."+ToolLeveling.MOD_ID+".tool_leveling_table");
	public ItemStackHandler inventory = new ItemStackHandler(1) {
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
        if(tag.contains("Inventory")) {
        	this.inventory.deserializeNBT(tag.getCompound("Inventory"));
        	
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        tag.put("Inventory", this.inventory.serializeNBT());
		return tag;
    }
	
	@Override
	public ITextComponent getDisplayName() {
	      return this.customname;
	}
	
	public IItemHandler getInventory() {
		return this.inventory;
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity playerEntity) {
		return new ToolLevelingTableContainer(windowId, playerInv, this);
	}
		
}
