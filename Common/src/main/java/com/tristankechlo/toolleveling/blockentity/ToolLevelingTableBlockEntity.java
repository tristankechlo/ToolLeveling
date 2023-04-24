package com.tristankechlo.toolleveling.blockentity;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import com.tristankechlo.toolleveling.util.Predicates;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class ToolLevelingTableBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {

    private static final Component CONTAINER_NAME = Component.translatable("container." + ToolLeveling.MOD_ID + "." + ToolLeveling.TABLE_NAME);
    private NonNullList<ItemStack> items = NonNullList.withSize(NUMBER_OF_SLOTS, ItemStack.EMPTY);
    public static final int NUMBER_OF_SLOTS = 7;
    public static final int[] SLOTS = IntStream.range(1, NUMBER_OF_SLOTS).toArray();

    public ToolLevelingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ToolLeveling.TLT_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(tag, this.items);
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        ContainerHelper.saveAllItems(tag, this.items);
    }

    @Override
    protected Component getDefaultName() {
        return CONTAINER_NAME;
    }

    @Override
    protected AbstractContainerMenu createMenu(int windowId, Inventory playerInv) {
        return new ToolLevelingTableMenu(windowId, playerInv, this, this.worldPosition);
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
        if (index < 0 || index >= this.getContainerSize()) {
            return ItemStack.EMPTY;
        }
        return this.items.get(index);
    }

    @Override
    public ItemStack removeItem(int var1, int var2) {
        return ContainerHelper.removeItem(this.items, var1, var2);
    }

    @Override
    public ItemStack removeItemNoUpdate(int index) {
        ItemStack stack = this.items.get(index);
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.items.set(index, ItemStack.EMPTY);
            return stack;
        }
    }

    @Override
    public void setItem(int index, ItemStack stack) {
        this.items.set(index, stack);
        if (!stack.isEmpty() && stack.getCount() > this.getMaxStackSize()) {
            stack.setCount(this.getMaxStackSize());
        }
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    @Override
    public boolean canPlaceItem(int index, ItemStack stack) {
        if (index == 0) {
            return Predicates.UPGRADE.test(stack);
        }
        if (index >= 1 && index <= 3) {
            return Predicates.PAYMENT.test(stack);
        }
        if (index >= 4 && index < NUMBER_OF_SLOTS) {
            return Predicates.BOOK.test(stack);
        }
        return false;
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public int[] getSlotsForFace(Direction var1) {
        return SLOTS;
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, ItemStack var2, Direction var3) {
        return index != 0 && this.canPlaceItem(index, var2);
    }

    @Override
    public boolean canTakeItemThroughFace(int index, ItemStack var2, Direction var3) {
        return this.canPlaceItemThroughFace(index, var2, var3);
    }

    public ItemStack getStackToEnchant() {
        return this.getItem(0);
    }

    public Optional<Enchantment> getRandomEnchantment(RandomSource random) {
        SimpleWeightedRandomList.Builder<Enchantment> builder = new SimpleWeightedRandomList.Builder<>();

        for (int i = 4; i < 7; i++) {
            ItemStack stack = this.getItem(i);
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            enchantments.forEach(builder::add);
        }

        SimpleWeightedRandomList<Enchantment> enchantmentWeights = builder.build();
        Optional<WeightedEntry.Wrapper<Enchantment>> result = enchantmentWeights.getRandom(random);
        return result.map(WeightedEntry.Wrapper::getData);
    }

}
