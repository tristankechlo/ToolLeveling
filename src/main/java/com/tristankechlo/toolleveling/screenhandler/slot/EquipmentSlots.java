package com.tristankechlo.toolleveling.screenhandler.slot;

import com.mojang.datafixers.util.Pair;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;

public class EquipmentSlots extends Slot {

	private static final Identifier[] ARMOR_SLOT_TEXTURES = new Identifier[] {
			PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE,
			PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE };
	private final EquipmentSlot equipmentSlotType;

	public EquipmentSlots(Inventory inventory, int x, int y, EquipmentSlot equipmentSlotType) {
		super(inventory, 36 + equipmentSlotType.getEntitySlotId(), x, y);
		this.equipmentSlotType = equipmentSlotType;
	}

	@Override
	public boolean canInsert(ItemStack stack) {
		return MobEntity.getPreferredEquipmentSlot(stack) == this.equipmentSlotType;
	}

	@Override
	public boolean canTakeItems(PlayerEntity playerEntity) {
		ItemStack itemStack = getStack();
		if (!itemStack.isEmpty() && !playerEntity.isCreative() && EnchantmentHelper.hasBindingCurse(itemStack)) {
			return false;
		}
		return super.canTakeItems(playerEntity);
	}

	@Override
	public int getMaxItemCount() {
		return 1;
	}

	@Override
	public Pair<Identifier, Identifier> getBackgroundSprite() {
		return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE,
				ARMOR_SLOT_TEXTURES[equipmentSlotType.getEntitySlotId()]);
	}

}
