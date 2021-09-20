package com.tristankechlo.toolleveling.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

	@Inject(at = @At("HEAD"), method = "getEnchantmentLevel", cancellable = true)
	private static void getEnchantmentLevel(CompoundTag tag, CallbackInfoReturnable<Integer> callback) {
		callback.setReturnValue(Mth.clamp(tag.getInt("lvl"), 0, Short.MAX_VALUE));
	}

}
