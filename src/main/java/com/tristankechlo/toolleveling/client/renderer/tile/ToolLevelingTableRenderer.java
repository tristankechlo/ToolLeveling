package com.tristankechlo.toolleveling.client.renderer.tile;

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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToolLevelingTableRenderer implements BlockEntityRenderer<ToolLevelingTableBlockEntity> {

	private final Quaternion quaternion = new Quaternion(Vector3f.XN, 1.5707F, false);

	public ToolLevelingTableRenderer(BlockEntityRendererProvider.Context rendererDispatcherIn) {}

	@Override
	public void render(ToolLevelingTableBlockEntity tileEntityIn, float partialTicks, PoseStack matrixStackIn,
			MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {

		ItemStack stack = tileEntityIn.getStackToEnchant();
		if (!stack.isEmpty()) {
			matrixStackIn.pushPose();
			matrixStackIn.translate(0.5D, 0.83D, 0.5D);
			matrixStackIn.scale(0.6F, 0.6F, 0.6F);
			matrixStackIn.mulPose(quaternion);
			renderItem(stack, partialTicks, matrixStackIn, bufferIn, combinedLightIn, combinedOverlayIn);
			matrixStackIn.popPose();

		}
	}

	private void renderItem(ItemStack stack, float partialTicks, PoseStack pstack, MultiBufferSource bufferIn,
			int combinedLightIn, int combinedOverlayIn) {
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.FIXED, combinedLightIn,
				combinedOverlayIn, pstack, bufferIn, OverlayTexture.NO_OVERLAY);
	}

}
