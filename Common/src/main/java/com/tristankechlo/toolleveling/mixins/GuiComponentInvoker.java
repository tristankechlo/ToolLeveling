package com.tristankechlo.toolleveling.mixins;

import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.gui.GuiComponent;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiComponent.class)
public interface GuiComponentInvoker {

    @Invoker("fillGradient")
    static void fillGradient(Matrix4f $$0, BufferBuilder $$1, int x1, int y1, int x2, int y2, int m, int color1, int color2) {
        throw new AssertionError();
    }

}
