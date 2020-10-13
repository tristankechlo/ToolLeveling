package com.tristankechlo.toolleveling.client.renderer.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tristankechlo.toolleveling.blocks.EnchantmentPillarBlock;
import com.tristankechlo.toolleveling.tileentity.EnchantmentPillarTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EnchantmentPillarRenderer extends TileEntityRenderer<EnchantmentPillarTileEntity> {

	private float degrees;

	public EnchantmentPillarRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
		super(rendererDispatcherIn);
		this.degrees = 0.0F;
	}

	@Override
	public void render(EnchantmentPillarTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn,	IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
		if(tileEntityIn.getBlockState().get(EnchantmentPillarBlock.HALF) == DoubleBlockHalf.UPPER) {
			return;
		}
		ItemStack stack = tileEntityIn.inventory.getStackInSlot(0);
		if (!stack.isEmpty()) {
			matrixStackIn.push();
			matrixStackIn.translate(0.5D, 1.75D, 0.5D);
			matrixStackIn.scale(0.45F, 0.45F, .45F);
			float currentTime = tileEntityIn.getWorld().getGameTime() + partialTicks;
			matrixStackIn.translate(0D, (Math.sin(Math.PI * currentTime / 24) / 6) + 0.1D, 0D);
			matrixStackIn.rotate(Vector3f.YP.rotationDegrees(degrees++ / 3));
			renderItem(stack, partialTicks, matrixStackIn, bufferIn, combinedLightIn);
			matrixStackIn.pop();

		}

	}

	private void renderItem(ItemStack stack, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn,	int combinedLightIn) {
		Minecraft.getInstance().getItemRenderer().renderItem(stack, TransformType.FIXED, combinedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
	}

}
