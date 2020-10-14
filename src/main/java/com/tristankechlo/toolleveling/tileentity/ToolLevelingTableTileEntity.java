package com.tristankechlo.toolleveling.tileentity;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blocks.EnchantmentPillarBlock;
import com.tristankechlo.toolleveling.container.ToolLevelingTableContainer;
import com.tristankechlo.toolleveling.init.ModTileEntities;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;

public class ToolLevelingTableTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {

	private ITextComponent customname = new TranslationTextComponent("container." + ToolLeveling.MOD_ID + ".tool_leveling_table");
	public ItemStackHandler inventory = new ItemStackHandler(1){
		@Override
		protected void onContentsChanged(int slot){
			markDirty();
		};
	};
	private int tickCounter;
	private int reachableItems;

	public ToolLevelingTableTileEntity() {
		super(ModTileEntities.TOOL_LEVELING_TABLE.get());
	}

	@Override
	public void tick() {
		tickCounter++;
		if(tickCounter % 20 == 0) {
			tickCounter = 0;
			checkForNearbyPillars();
		}
	}

	public void checkForNearbyPillars() {
		reachableItems = 0;
		BlockPos position = this.getPos();
		Iterable<BlockPos> nearby = BlockPos.getAllInBoxMutable(position.getX()-4, position.getY(), position.getZ()-4, position.getX()+4, position.getY(), position.getZ()+4);
		for(BlockPos pos : nearby) {
			TileEntity entity = world.getTileEntity(pos);
			if(entity != null && (entity instanceof EnchantmentPillarTileEntity)) {
				EnchantmentPillarTileEntity pillar = (EnchantmentPillarTileEntity)entity;
				ItemStack stack = pillar.inventory.getStackInSlot(0);
				if(!stack.isEmpty()) {
					reachableItems += stack.getCount();
				}
			}
		}
		//ToolLeveling.LOGGER.debug("found " + reachableItems);
	}
	
	public int getReachableItems() {
		return reachableItems;
	}
	
	public boolean removeItemsFromNearbyPillars(int amount) {
		BlockPos position = this.getPos();
		Iterable<BlockPos> nearby = BlockPos.getAllInBoxMutable(position.getX()-4, position.getY(), position.getZ()-4, position.getX()+4, position.getY(), position.getZ()+4);
		for(BlockPos pos : nearby) {
			if(amount <= 0) {
				break;
			}
			TileEntity entity = world.getTileEntity(pos);
			if(entity != null && (entity instanceof EnchantmentPillarTileEntity)) {
				EnchantmentPillarTileEntity pillar = (EnchantmentPillarTileEntity)entity;
				ItemStack oldstack = pillar.inventory.getStackInSlot(0);
				if(!oldstack.isEmpty()) {
					
					if(oldstack.getCount() >= amount) {
						ItemStack newstack = oldstack.copy();
						newstack.shrink(amount);
						amount = 0;
						pillar.inventory.setStackInSlot(0, newstack);
						world.setBlockState(pos, world.getBlockState(pos).with(EnchantmentPillarBlock.ACTIVE, false));
						world.setBlockState(pos.up(), world.getBlockState(pos.up()).with(EnchantmentPillarBlock.ACTIVE, false));
					} else if(oldstack.getCount() < amount) {
						amount -= oldstack.getCount();
						pillar.inventory.setStackInSlot(0, ItemStack.EMPTY);
						world.setBlockState(pos, world.getBlockState(pos).with(EnchantmentPillarBlock.ACTIVE, false));
						world.setBlockState(pos.up(), world.getBlockState(pos.up()).with(EnchantmentPillarBlock.ACTIVE, false));
					}
				}
			}
		}
		if(amount <= 0) {
			return true;
		}
		return false;
	}

	@Override
	public void read(BlockState state, CompoundNBT tag) {
		super.read(state, tag);
		if (tag.contains("Inventory")) {
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

	public ItemStack getStackToEnchant() {
		return this.inventory.getStackInSlot(0);
	}

	@Override
	public Container createMenu(int windowId, PlayerInventory playerInv, PlayerEntity playerEntity) {
		return new ToolLevelingTableContainer(windowId, playerInv, this);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), Constants.BlockFlags.BLOCK_UPDATE);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return new SUpdateTileEntityPacket(this.getPos(), 1, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.read(getBlockState(), pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		this.read(state, tag);
	}

}
