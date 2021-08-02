package com.tristankechlo.toolleveling.container.slot;

import com.mojang.datafixers.util.Pair;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EquipmentSlot extends Slot {

	private static final ResourceLocation[] ARMOR_SLOT_TEXTURES = new ResourceLocation[] {
			PlayerContainer.EMPTY_ARMOR_SLOT_BOOTS, PlayerContainer.EMPTY_ARMOR_SLOT_LEGGINGS,
			PlayerContainer.EMPTY_ARMOR_SLOT_CHESTPLATE, PlayerContainer.EMPTY_ARMOR_SLOT_HELMET };
	private final EquipmentSlotType equipmentSlotType;
	private final PlayerEntity player;

	public EquipmentSlot(IInventory inventory, int x, int y, EquipmentSlotType equipmentSlotType, PlayerEntity player) {
		super(inventory, 36 + equipmentSlotType.getIndex(), x, y);
		this.equipmentSlotType = equipmentSlotType;
		this.player = player;
	}

	@Override
	public int getMaxStackSize() {
		return 1;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
		return stack.canEquip(equipmentSlotType, player);
	}

	@Override
	public boolean mayPickup(PlayerEntity playerIn) {
		ItemStack itemstack = this.getItem();
		return !itemstack.isEmpty() && !playerIn.isCreative() && EnchantmentHelper.hasBindingCurse(itemstack) ? false
				: super.mayPickup(playerIn);
	}

	@OnlyIn(Dist.CLIENT)
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return Pair.of(PlayerContainer.BLOCK_ATLAS, ARMOR_SLOT_TEXTURES[equipmentSlotType.getIndex()]);
	}

}
