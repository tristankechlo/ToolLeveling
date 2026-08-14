package com.tristankechlo.toolleveling.client.blockentityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;

public class ToolLevelingTableRenderer<E extends ToolLevelingTableBlockEntity> implements BlockEntityRenderer<E> {

    private final Quaternionf quaternion = Axis.XN.rotation(1.5707F);

    public ToolLevelingTableRenderer(BlockEntityRendererProvider.Context rendererDispatcher) {}

    @Override
    public void render(ToolLevelingTableBlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        ItemStack stack = blockEntity.getStackToEnchant();
        if (!stack.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.5D, 0.83D, 0.5D);
            poseStack.scale(0.6F, 0.6F, 0.6F);
            poseStack.mulPose(quaternion);
            renderItem(stack, partialTicks, poseStack, buffer, combinedLight, combinedOverlay);
            poseStack.popPose();

        }
    }

    private void renderItem(ItemStack stack, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemDisplayContext.FIXED, combinedLight, combinedOverlay, poseStack, buffer, null, OverlayTexture.NO_OVERLAY);
    }

}
