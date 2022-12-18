package com.tristankechlo.toolleveling.tileentity;

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

import javax.annotation.Nullable;

public class ToolLevelingTableTileEntity extends TileEntity implements INamedContainerProvider {

    private ITextComponent customname = new TranslationTextComponent("container." + Names.MOD_ID + ".tool_leveling_table");
    public final ChestContents chestContents;
    public static final int NUMBER_OF_SLOTS = 16;
    public long bonusPoints = 0;

    public ToolLevelingTableTileEntity() {
        super(ModRegistry.TLT_TILE_ENTITY.get());
        chestContents = ChestContents.createForTileEntity(NUMBER_OF_SLOTS, this::canPlayerAccess, this::setChanged);
    }

    public boolean canPlayerAccess(PlayerEntity player) {
        if (this.level.getBlockEntity(this.worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) < (8.0 * 8.0);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        chestContents.deserializeNBT(tag.getCompound("Inventory"));
        if (chestContents.getContainerSize() != NUMBER_OF_SLOTS) {
            throw new IllegalArgumentException("Corrupted NBT: Number of inventory slots did not match expected.");
        }
        this.bonusPoints = tag.getLong("BonusPoints");
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.put("Inventory", chestContents.serializeNBT());
        tag.putLong("BonusPoints", this.bonusPoints);
        return tag;
    }

    public ItemStack getStackToEnchant() {
        return this.chestContents.getItem(0);
    }

    public long getInventoryWorth() {
        long worth = 0;
        for (int i = 1; i < this.chestContents.getContainerSize(); i++) {
            ItemStack stack = this.chestContents.getItem(i);
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
        for (int i = 1; i < this.chestContents.getContainerSize(); i++) {
            ItemStack stack = this.chestContents.getItem(i).copy();
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
            this.chestContents.setItem(i, stack);
        }
        this.setChanged();
        return true;
    }

    public void dropAllContents(World world, BlockPos blockPos) {
        InventoryHelper.dropContents(world, blockPos, chestContents);
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.customname;
    }

    @Nullable
    @Override
    public Container createMenu(int windowID, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return ToolLevelingTableContainer.createForServerSide(windowID, playerInventory, chestContents, worldPosition);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);
        return new SUpdateTileEntityPacket(this.getBlockPos(), 42, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        BlockState blockState = level.getBlockState(worldPosition);
        this.load(blockState, pkt.getTag());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = new CompoundNBT();
        this.save(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.load(state, tag);
    }

    @Override
    public void setChanged() {
        super.setChanged();
        this.level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

}
