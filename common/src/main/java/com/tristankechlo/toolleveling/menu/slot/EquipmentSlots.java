package com.tristankechlo.toolleveling.menu.slot;

import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EquipmentSlots extends Slot {

    private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[]{
            InventoryMenu.EMPTY_ARMOR_SLOT_BOOTS, InventoryMenu.EMPTY_ARMOR_SLOT_LEGGINGS,
            InventoryMenu.EMPTY_ARMOR_SLOT_CHESTPLATE, InventoryMenu.EMPTY_ARMOR_SLOT_HELMET};
    private final EquipmentSlot equipmentSlotType;

    public EquipmentSlots(Container inventory, int x, int y, EquipmentSlot equipmentSlotType) {
        super(inventory, 36 + equipmentSlotType.getIndex(), x, y);
        this.equipmentSlotType = equipmentSlotType;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        return Mob.getEquipmentSlotForItem(stack) == equipmentSlotType;
    }

    @Override
    public boolean mayPickup(Player player) {
        ItemStack stack = this.getItem();
        if (!stack.isEmpty() && !player.isCreative() && EnchantmentHelper.hasBindingCurse(stack)) {
            return false;
        }
        return super.mayPickup(player);
    }

    @Override
    public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
        return Pair.of(InventoryMenu.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[equipmentSlotType.getIndex()]);
    }

}
