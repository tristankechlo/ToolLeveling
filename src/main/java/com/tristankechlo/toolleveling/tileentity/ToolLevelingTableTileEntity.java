package com.tristankechlo.toolleveling.tileentity;

import javax.annotation.Nullable;

import com.tristankechlo.toolleveling.container.ToolLevelingTableContainer;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.utils.ChestContents;
import com.tristankechlo.toolleveling.utils.Names;
import com.tristankechlo.toolleveling.utils.Utils;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class ToolLevelingTableTileEntity extends TileEntity implements INamedContainerProvider {

	private ITextComponent customname = new TranslationTextComponent("container." + Names.MOD_ID + ".tool_leveling_table");
	public final ChestContents chestContents;
	public static final int NUMBER_OF_SLOTS = 16;
	public int bonusPoints = 0;

	public ToolLevelingTableTileEntity() {
		super(ModRegistry.TLT_TILE_ENTITY.get());
		chestContents = ChestContents.createForTileEntity(NUMBER_OF_SLOTS, this::canPlayerAccess, this::markDirty);
	}

	public boolean canPlayerAccess(PlayerEntity player) {
		if (this.world.getTileEntity(this.pos) != this) {
			return false;
		}
		return player.getDistanceSq(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) < (8.0 * 8.0);
	}

	@Override
	public void read(BlockState state, CompoundNBT tag) {
		super.read(state, tag);
		chestContents.deserializeNBT(tag.getCompound("Inventory"));
		if (chestContents.getSizeInventory() != NUMBER_OF_SLOTS) {
			throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected.");
		}
		this.bonusPoints = tag.getInt("BonusPoints");
	}

	@Override
	public CompoundNBT write(CompoundNBT tag) {
		super.write(tag);
		tag.put("Inventory", chestContents.serializeNBT());
		tag.putInt("BonusPoints", this.bonusPoints);
		return tag;
	}

	public ItemStack getStackToEnchant() {
		return this.chestContents.getStackInSlot(0);
	}

	public int getInventoryWorth() {
		int worth = 0;
		for (int i = 1; i < this.chestContents.getSizeInventory(); i++) {
			ItemStack stack = this.chestContents.getStackInSlot(i);
			if (!stack.isEmpty()) {
				worth += Utils.getStackWorth(stack);
			}
		}
		return worth;
	}

	public boolean decreaseInventoryWorth(int upgradeCost) {
		int invWorth = this.getInventoryWorth() + this.bonusPoints;
		if (upgradeCost > invWorth) {
			return false;
		}
		// enough points stored as bonusPoints
		if (upgradeCost <= bonusPoints) {
			bonusPoints -= upgradeCost;
			this.markDirty();
			return true;
		}
		upgradeCost -= bonusPoints;
		bonusPoints = 0;
		for (int i = 1; i < this.chestContents.getSizeInventory(); i++) {
			ItemStack stack = this.chestContents.getStackInSlot(i).copy();
			if (stack.isEmpty() || upgradeCost <= 0) {
				continue;
			}
//			ToolLeveling.LOGGER.debug(i);
			int stackWorth = Utils.getStackWorth(stack);
			if (stackWorth <= upgradeCost) {
				upgradeCost -= stackWorth;
				stack = ItemStack.EMPTY;
			} else {
				int itemWorth = Utils.getItemWorth(stack);
				int stackAmount = stack.getCount();
				for (int j = 0; j < stack.getCount(); j++) {
					if (upgradeCost <= 0 || stackAmount == 0) {
						break;
					}
					if (itemWorth > upgradeCost) {
						stackAmount--;
						bonusPoints = Math.abs(upgradeCost - itemWorth);
						upgradeCost = 0;
					} else {
						stackAmount--;
						upgradeCost -= itemWorth;
					}
				}
				stack.setCount(stackAmount);
			}
			this.chestContents.setInventorySlotContents(i, stack);
		}
		this.markDirty();
		return true;
	}

	public void dropAllContents(World world, BlockPos blockPos) {
		InventoryHelper.dropInventoryItems(world, blockPos, chestContents);
	}

	@Override
	public ITextComponent getDisplayName() {
		return this.customname;
	}

	@Nullable
	@Override
	public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
		return ToolLevelingTableContainer.createForServerSide(windowID, playerInventory, chestContents, pos);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		write(nbt);
		return new SUpdateTileEntityPacket(this.getPos(), 42, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		BlockState blockState = world.getBlockState(pos);
		this.read(blockState, pkt.getNbtCompound());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = new CompoundNBT();
		write(nbt);
		return nbt;
	}

	@Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		this.read(state, tag);
	}
	
	@Override
	public void markDirty() {
		super.markDirty();
		this.world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 3);
	}

}
