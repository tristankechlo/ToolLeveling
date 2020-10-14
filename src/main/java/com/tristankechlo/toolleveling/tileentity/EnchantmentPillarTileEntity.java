package com.tristankechlo.toolleveling.tileentity;

import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import com.tristankechlo.toolleveling.init.ModTileEntities;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;

public class EnchantmentPillarTileEntity extends TileEntity {

	public ItemStackHandler inventory = new ItemStackHandler(1) {

		@Override
		public boolean isItemValid(int slot, ItemStack stack) {
			return (stack.getItem() == ForgeRegistries.ITEMS.getValue(new ResourceLocation(ToolLevelingConfig.SERVER.upgradeItem.get())));
		};
		
		@Override
		protected void onContentsChanged(int slot) {
			markDirty();
		};
	};

	public EnchantmentPillarTileEntity() {
		super(ModTileEntities.ENCHANTING_PILLAR.get());
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		if (nbt.contains("Inventory")) {
			inventory.deserializeNBT(nbt.getCompound("Inventory"));
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		compound.put("Inventory", inventory.serializeNBT());
		return compound;
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
