package com.tristankechlo.toolleveling.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ToolLevelingTableRenderer implements BlockEntityRenderer<ToolLevelingTableBlockEntity> {

    private final ItemRenderer itemRenderer;

    public ToolLevelingTableRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(ToolLevelingTableBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        ItemStack stack = entity.getStackToEnchant();
        if (stack.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5D, 0.83D, 0.5D);
        poseStack.scale(0.7F, 0.7F, 0.7F);
        poseStack.mulPose(Axis.XN.rotation(1.5707F));
        renderItem(stack, poseStack, buffer, light, overlay);
        poseStack.popPose();
    }

    private void renderItem(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, light, overlay, poseStack, buffer, null, OverlayTexture.NO_OVERLAY);
    }

}
