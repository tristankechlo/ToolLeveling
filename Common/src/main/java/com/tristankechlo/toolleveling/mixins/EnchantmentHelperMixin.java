package com.tristankechlo.toolleveling.mixins;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Inject(at = @At("HEAD"), method = "getEnchantmentLevel(Lnet/minecraft/nbt/CompoundTag;)I", cancellable = true)
    private static void ToolLeveling$getEnchantmentLevel(CompoundTag nbt, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Mth.clamp(nbt.getInt("lvl"), 0, Short.MAX_VALUE));
    }

}
