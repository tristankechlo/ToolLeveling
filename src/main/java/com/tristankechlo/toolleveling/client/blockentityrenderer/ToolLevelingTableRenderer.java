package com.tristankechlo.toolleveling.client.blockentityrenderer;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class ToolLevelingTableRenderer implements BlockEntityRenderer<ToolLevelingTableBlockEntity> {

    public ToolLevelingTableRenderer(BlockEntityRendererFactory.Context dispatcher) {}

    @Override
    public void render(ToolLevelingTableBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack stack = entity.getStackToEnchant();
        if (!stack.isEmpty()) {
            matrices.push();
            matrices.translate(0.5D, 0.83D, 0.5D);
            matrices.scale(0.6F, 0.6F, 0.6F);
            matrices.multiply(RotationAxis.NEGATIVE_X.rotation(1.5707F));
            renderItem(stack, tickDelta, matrices, vertexConsumers, light, overlay);
            matrices.pop();
        }
    }

    private void renderItem(ItemStack stack, float partialTicks, MatrixStack matrices,
                            VertexConsumerProvider vertexConsumers, int light, int overlay) {
        MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformationMode.FIXED, light,
                overlay, matrices, vertexConsumers, null, OverlayTexture.DEFAULT_UV);
    }

}
