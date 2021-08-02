package com.tristankechlo.toolleveling.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ToolLevelingTableRenderer extends TileEntityRenderer<ToolLevelingTableTileEntity> {

	public ToolLevelingTableRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
	}

	@Override
	public void render(ToolLevelingTableTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,
			IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {

		ItemStack stack = tileEntityIn.getStackToEnchant();
		if (!stack.isEmpty()) {
			matrixStackIn.pushPose();
			matrixStackIn.translate(0.5D, 0.89D, 0.5D);
			matrixStackIn.scale(0.5F, 0.5F, 0.5F);
			matrixStackIn.mulPose(new Quaternion(Vector3f.XN, 1.5707F, false));
			renderItem(stack, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
			matrixStackIn.popPose();

		}
	}

	private void renderItem(ItemStack stack, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,
			int combinedLightIn) {
		Minecraft.getInstance().getItemRenderer().renderStatic(stack, TransformType.FIXED, combinedLightIn,
				OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
	}

}
