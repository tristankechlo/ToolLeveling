package com.tristankechlo.toolleveling.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ItemValueScreen extends Screen {

	private static final ITextComponent TITLE = new TranslationTextComponent("block.toolleveling.item_values");

	public ItemValueScreen() {
		super(TITLE);
	}

	@Override
	protected void init() {
		super.init();
	}

	@Override
	public void tick() {
		super.tick();
	}

	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
		this.renderBackground(matrixStack); // render translucent grey background
		super.render(matrixStack, mouseX, mouseY, partialTicks); // render buttons
	}

}
