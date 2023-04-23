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

    public ToolLevelingTableRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {
        this.itemRenderer = rendererDispatcherIn.getItemRenderer();
    }

    @Override
    public void render(ToolLevelingTableBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack stack = tileEntityIn.getStackToEnchant();
        if (!stack.isEmpty()) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5D, 0.83D, 0.5D);
            matrixStackIn.scale(0.6F, 0.6F, 0.6F);
            matrixStackIn.mulPose(Axis.XN.rotation(1.5707F));
            renderItem(stack, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
            matrixStackIn.popPose();

        }
    }

    private void renderItem(ItemStack stack, PoseStack pstack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        itemRenderer.renderStatic(stack, ItemDisplayContext.FIXED, combinedLightIn, combinedOverlayIn, pstack, bufferIn, null, OverlayTexture.NO_OVERLAY);
    }

}
