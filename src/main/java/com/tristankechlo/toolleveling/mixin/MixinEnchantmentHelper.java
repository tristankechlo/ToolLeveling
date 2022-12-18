package com.tristankechlo.toolleveling.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentHelper.class)
public abstract class MixinEnchantmentHelper {

    @Redirect(method = "getItemEnchantmentLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;clamp(III)I"))
    private static int getItemEnchantmentLevel(int level, int min, int max) {
        return MathHelper.clamp(level, min, Short.MAX_VALUE);
    }

}
