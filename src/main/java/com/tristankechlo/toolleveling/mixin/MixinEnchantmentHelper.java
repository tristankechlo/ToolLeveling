package com.tristankechlo.toolleveling.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {

	/* override the vanilla behaviour of clamping the value between 0-255 */
	@Inject(at = @At("HEAD"), method = "getLevelFromNbt", cancellable = true)
	private static void getLevelFromNbt(NbtCompound tag, CallbackInfoReturnable<Integer> callback) {
		callback.setReturnValue(MathHelper.clamp(tag.getInt("lvl"), 0, Short.MAX_VALUE));
	}

}
