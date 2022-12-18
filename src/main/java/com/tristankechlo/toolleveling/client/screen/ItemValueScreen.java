package com.tristankechlo.toolleveling.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.tristankechlo.toolleveling.client.screen.widgets.ItemValuesListWidget;
import com.tristankechlo.toolleveling.config.ItemValueConfig;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemValueScreen extends Screen {

    private static final int SPACING = 30;
    private static final ITextComponent TITLE = new TranslationTextComponent("block.toolleveling.item_values");
    private ItemValuesListWidget itemValues;

    public ItemValueScreen() {
        super(TITLE);
    }

    @Override
    protected void init() {
        super.init();
        this.itemValues = new ItemValuesListWidget(this, width - (2 * SPACING), SPACING, height - SPACING);
        this.itemValues.setLeftPos(SPACING);
        this.addWidget(itemValues);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack); // render translucent grey background
        this.itemValues.render(matrixStack, mouseX, mouseY, partialTicks); // render item list widget
        super.render(matrixStack, mouseX, mouseY, partialTicks); // render buttons
        AbstractGui.drawCenteredString(matrixStack, font,
                new TranslationTextComponent("screen.toolleveling.default_item_value_worth",
                        ItemValueConfig.defaultItemWorth.getValue()), width / 2, 5, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonID) {
        this.itemValues.mouseClicked(mouseX, mouseY, buttonID);
        return super.mouseClicked(mouseX, mouseY, buttonID);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.itemValues.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    public FontRenderer getFontRenderer() {
        return this.font;
    }

}
