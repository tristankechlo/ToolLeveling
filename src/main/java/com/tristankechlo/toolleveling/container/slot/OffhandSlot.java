package com.tristankechlo.toolleveling.container.slot;

import com.mojang.datafixers.util.Pair;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OffhandSlot extends Slot {

	public OffhandSlot(IInventory inventory, int x, int y) {
		super(inventory, 40, x, y);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
		return Pair.of(PlayerContainer.BLOCK_ATLAS, PlayerContainer.EMPTY_ARMOR_SLOT_SHIELD);
	}

}
