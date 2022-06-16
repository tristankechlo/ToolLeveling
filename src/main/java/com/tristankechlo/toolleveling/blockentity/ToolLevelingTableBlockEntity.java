package com.tristankechlo.toolleveling.blockentity;

import java.util.stream.IntStream;

import com.google.common.base.Preconditions;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.screenhandler.ToolLevelingTableScreenhandler;
import com.tristankechlo.toolleveling.utils.Names;
import com.tristankechlo.toolleveling.utils.Utils;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class ToolLevelingTableBlockEntity extends BlockEntity
		implements Inventory, SidedInventory, NamedScreenHandlerFactory, ExtendedScreenHandlerFactory {

	private static final Text CUSTOMNAME = Text.translatable("container." + Names.MOD_ID + ".tool_leveling_table");
	private DefaultedList<ItemStack> items = DefaultedList.ofSize(NUMBER_OF_SLOTS, ItemStack.EMPTY);
	public static final int NUMBER_OF_SLOTS = 16;
	public static final int[] SLOTS = IntStream.range(1, NUMBER_OF_SLOTS).toArray();
	public long bonusPoints = 0;

	public ToolLevelingTableBlockEntity(BlockPos pos, BlockState state) {
		super(ToolLeveling.TLT_BLOCK_ENTITY, pos, state);
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		Inventories.writeNbt(nbt, items);
		nbt.putLong("BonusPoints", this.bonusPoints);
		super.writeNbt(nbt);
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		this.items = DefaultedList.ofSize(NUMBER_OF_SLOTS, ItemStack.EMPTY);
		Inventories.readNbt(nbt, this.items);
		this.bonusPoints = nbt.getLong("BonusPoints");
	}

	@Override
	public int[] getAvailableSlots(Direction side) {
		return SLOTS;
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir) {
		return false;
	}

	@Override
	public boolean canInsert(int index, ItemStack stack, Direction arg2) {
		return (index == 0) ? false : (!stack.hasEnchantments() && !stack.isDamageable());
	}

	@Override
	public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
		return new ToolLevelingTableScreenhandler(syncId, playerInventory, this, pos);
	}

	@Override
	public Text getDisplayName() {
		return CUSTOMNAME;
	}

	@Override
	public void clear() {
		items.clear();
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player) {
		if (this.world.getBlockEntity(this.pos) != this) {
			return false;
		}
		if (player.squaredDistanceTo(this.pos.getX() + 0.5D, this.pos.getY() + 0.5D, this.pos.getZ() + 0.5D) > 64.0D) {
			return false;
		}
		return true;
	}

	@Override
	public ItemStack getStack(int slot) {
		if (slot < 0 || slot >= items.size()) {
			return ItemStack.EMPTY;
		}
		return this.items.get(slot);
	}

	@Override
	public boolean isEmpty() {
		return this.items.stream().allMatch(ItemStack::isEmpty);
	}

	@Override
	public ItemStack removeStack(int slot) {
		ItemStack itemStack = this.items.get(slot);
		if (itemStack.isEmpty()) {
			return ItemStack.EMPTY;
		}
		this.items.set(slot, ItemStack.EMPTY);
		return itemStack;
	}

	@Override
	public ItemStack removeStack(int slot, int amount) {
		ItemStack itemStack = Inventories.splitStack(items, slot, amount);
		if (!itemStack.isEmpty()) {
			this.markDirty();
		}
		return itemStack;
	}

	@Override
	public void setStack(int slot, ItemStack stack) {
		this.items.set(slot, stack);
		if (!stack.isEmpty() && stack.getCount() > this.getMaxCountPerStack()) {
			stack.setCount(this.getMaxCountPerStack());
		}
		this.markDirty();
	}

	@Override
	public int size() {
		return NUMBER_OF_SLOTS;
	}

	public ItemStack getStackToEnchant() {
		return this.getStack(0);
	}

	public long getInventoryWorth() {
		long worth = 0;
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
			this.markDirty();
			return true;
		}
		upgradeCost -= bonusPoints;
		bonusPoints = 0;
		for (int i = 1; i < NUMBER_OF_SLOTS; i++) {
			ItemStack stack = this.items.get(i).copy();
			if (stack.isEmpty() || upgradeCost <= 0) {
				continue;
			}
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
		this.markDirty();
		return true;
	}

	@Override
	public Packet<ClientPlayPacketListener> toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		var nbt = super.toInitialChunkDataNbt();
		this.writeNbt(nbt);
		return nbt;
	}

	public final void sync() {
		Preconditions.checkNotNull(world);
		if (world.isClient) {
			throw new IllegalStateException("Cannot call sync() on the logical client!");
		}
		((ServerWorld) world).getChunkManager().markForUpdate(pos);
	}

	@Override
	public void markDirty() {
		super.markDirty();
		sync();
	}

	@Override
	public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
		buf.writeBlockPos(pos);
	}

}
