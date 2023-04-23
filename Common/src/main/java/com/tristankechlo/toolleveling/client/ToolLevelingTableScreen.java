package com.tristankechlo.toolleveling.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class ToolLevelingTableScreen extends AbstractContainerScreen<ToolLevelingTableMenu> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ToolLeveling.MOD_ID, "textures/gui/tool_leveling_table.png");

    public ToolLevelingTableScreen(ToolLevelingTableMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        // texture size
        this.imageWidth = 256;
        this.imageHeight = 256;
        // offset for player inv title
        this.inventoryLabelY += 35;
        // offset container title
        this.titleLabelX -= 1;
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    protected void containerTick() {
        super.containerTick();
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack); // render translucent grey background
        super.render(matrixStack, mouseX, mouseY, partialTicks);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mX, int my) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, imageWidth, imageHeight);
    }

}
