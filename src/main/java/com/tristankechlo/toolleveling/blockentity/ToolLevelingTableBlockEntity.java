package com.tristankechlo.toolleveling.blockentity;

import java.util.stream.IntStream;

import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import com.tristankechlo.toolleveling.utils.Names;
import com.tristankechlo.toolleveling.utils.Utils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class ToolLevelingTableBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

	private Component customname = new TranslatableComponent("container." + Names.MOD_ID + ".tool_leveling_table");
	private NonNullList<ItemStack> items = NonNullList.withSize(NUMBER_OF_SLOTS, ItemStack.EMPTY);
	public static final int NUMBER_OF_SLOTS = 16;
	public static final int[] SLOTS = IntStream.range(1, NUMBER_OF_SLOTS).toArray();
	public long bonusPoints = 0;

	public ToolLevelingTableBlockEntity(BlockPos pos, BlockState state) {
		super(ModRegistry.TLT_TILE_ENTITY.get(), pos, state);
	}

	public boolean canPlayerAccess(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		}
		return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D,
				worldPosition.getZ() + 0.5D) < (8.0 * 8.0);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items);
		this.bonusPoints = tag.getLong("BonusPoints");

	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		ContainerHelper.saveAllItems(tag, this.items);
		tag.putLong("BonusPoints", this.bonusPoints);
	}

	@Override
	public CompoundTag save(CompoundTag tag) {
		super.save(tag);
		ContainerHelper.saveAllItems(tag, this.items);
		tag.putLong("BonusPoints", this.bonusPoints);
		return tag;
	}

	public ItemStack getStackToEnchant() {
		if (this.items.get(0).is(Items.AIR)) {
			return ItemStack.EMPTY;
		}
		return this.items.get(0);
	}

	public int getInventoryWorth() {
		int worth = 0;
		for (int i = 1; i < NUMBER_OF_SLOTS; i++) {
			ItemStack stack = this.items.get(i);
			if (!stack.isEmpty()) {
				worth += Utils.getStackWorth(stack);
			}
		}
		return worth;
	}

	public boolean decreaseInventoryWorth(long upgradeCost) {
		long invWorth = this.getInventoryWorth() + this.bonusPoints;
		if (upgradeCost > invWorth) {
			return false;
		}
		// enough points stored as bonusPoints
		if (upgradeCost <= bonusPoints) {
			bonusPoints -= upgradeCost;
			this.setChanged();
			return true;
		}
		upgradeCost -= bonusPoints;
		bonusPoints = 0;
		for (int i = 1; i < NUMBER_OF_SLOTS; i++) {
			ItemStack stack = this.items.get(i).copy();
			if (stack.isEmpty() || upgradeCost <= 0) {
				continue;
			}
//			ToolLeveling.LOGGER.debug(i);
			long stackWorth = Utils.getStackWorth(stack);
			if (stackWorth <= upgradeCost) {
				upgradeCost -= stackWorth;
				stack = ItemStack.EMPTY;
			} else {
				long itemWorth = Utils.getItemWorth(stack);
				int stackCount = stack.getCount();
				for (int j = 0; j < stack.getCount(); j++) {
					if (upgradeCost <= 0 || stackCount == 0) {
						break;
					}
					if (itemWorth > upgradeCost) {
						stackCount--;
						bonusPoints = Math.abs(upgradeCost - itemWorth);
						upgradeCost = 0;
					} else {
						stackCount--;
						upgradeCost -= itemWorth;
					}
				}
				stack.setCount(stackCount);
			}
			this.items.set(i, stack);
		}
		this.setChanged();
		return true;
	}

	@Override
	public Component getName() {
		return this.customname;
	}

	@Override
	public int getContainerSize() {
		return NUMBER_OF_SLOTS;
	}

	@Override
	public boolean isEmpty() {
		return this.items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack getItem(int index) {
		return this.items.get(index);
	}

	@Override
	public ItemStack removeItem(int p_18942_, int p_18943_) {
		this.setChanged();
		return ContainerHelper.removeItem(this.items, p_18942_, p_18943_);
	}

	@Override
	public ItemStack removeItemNoUpdate(int p_18951_) {
		return ContainerHelper.takeItem(this.items, p_18951_);
	}

	@Override
	public void setItem(int index, ItemStack stack) {
		this.items.set(index, stack);
		if (stack.getCount() > this.getMaxStackSize()) {
			stack.setCount(this.getMaxStackSize());
		}
		this.setChanged();
	}

	@Override
	public boolean stillValid(Player player) {
		if (this.level.getBlockEntity(this.worldPosition) != this) {
			return false;
		} else {
			return !(player.distanceToSqr((double) this.worldPosition.getX() + 0.5D,
					(double) this.worldPosition.getY() + 0.5D, (double) this.worldPosition.getZ() + 0.5D) > 64.0D);
		}
	}

	@Override
	public void clearContent() {
		this.items.clear();
	}

	@Override
	protected Component getDefaultName() {
		return this.customname;
	}

	@Override
	public void setChanged() {
		super.setChanged();
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
	}

	@Override
	protected AbstractContainerMenu createMenu(int windowID, Inventory playerinv) {
		return new ToolLevelingTableMenu(windowID, playerinv, this, this.worldPosition);
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag tag = new CompoundTag();
		ContainerHelper.saveAllItems(tag, this.items);
		tag.putLong("BonusPoints", this.bonusPoints);
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag tag = pkt.getTag();
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items);
		this.bonusPoints = tag.getLong("BonusPoints");
	}

	@Override
	public CompoundTag getUpdateTag() {
		CompoundTag tag = super.getUpdateTag();
		ContainerHelper.saveAllItems(tag, this.items);
		tag.putLong("BonusPoints", this.bonusPoints);
		return tag;
	}

	@Override
	public void handleUpdateTag(CompoundTag tag) {
		super.handleUpdateTag(tag);
		this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
		ContainerHelper.loadAllItems(tag, this.items);
		this.bonusPoints = tag.getLong("BonusPoints");
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return SLOTS;
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, Direction side) {
		return (index == 0) ? false : this.canPlaceItem(index, stack);
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return index > 0 && !stack.isEnchanted() && !stack.isDamageableItem();
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction side) {
		return this.canPlaceItemThroughFace(index, stack, side);
	}

}
