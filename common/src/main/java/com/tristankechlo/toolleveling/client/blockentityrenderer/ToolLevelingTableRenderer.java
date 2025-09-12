package com.tristankechlo.toolleveling.client.blockentityrenderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;

public class ToolLevelingTableRenderer<E extends ToolLevelingTableBlockEntity> implements BlockEntityRenderer<E> {

    private final Quaternion quaternion = new Quaternion(Vector3f.XN, 1.5707F, false);

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
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.FIXED, combinedLight, combinedOverlay, poseStack, buffer, OverlayTexture.NO_OVERLAY);
    }

}
