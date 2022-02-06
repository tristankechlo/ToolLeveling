package com.tristankechlo.toolleveling.client.blockentityrenderer;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

@Environment(EnvType.CLIENT)
public class ToolLevelingTableRenderer implements BlockEntityRenderer<ToolLevelingTableBlockEntity> {

	public ToolLevelingTableRenderer(BlockEntityRendererFactory.Context dispatcher) {}

	@Override
	public void render(ToolLevelingTableBlockEntity entity, float tickDelta, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, int overlay) {
		ItemStack stack = entity.getStackToEnchant();
		if (!stack.isEmpty()) {
			matrices.push();
			matrices.translate(0.5D, 0.89D, 0.5D);
			matrices.scale(0.5F, 0.5F, 0.5F);
			matrices.multiply(new Quaternion(Vec3f.NEGATIVE_X, 1.5707F, false));
			renderItem(stack, tickDelta, matrices, vertexConsumers, light, overlay);
			matrices.pop();
		}
	}

	private void renderItem(ItemStack stack, float partialTicks, MatrixStack matrices,
			VertexConsumerProvider vertexConsumers, int light, int overlay) {
		MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.FIXED, light,
				overlay, matrices, vertexConsumers, OverlayTexture.DEFAULT_UV);
	}

}
