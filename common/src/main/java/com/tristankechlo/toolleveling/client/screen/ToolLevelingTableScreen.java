package com.tristankechlo.toolleveling.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.client.screen.widgets.ButtonListWidget;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@SuppressWarnings("removal") // forge in 1.20.6+
public class ToolLevelingTableScreen extends AbstractContainerScreen<ToolLevelingTableMenu> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ToolLeveling.MOD_ID, "textures/gui/tool_leveling_table.png");
    public ButtonListWidget buttonList;
    private byte ticksSinceUpdate = 0;

    public ToolLevelingTableScreen(ToolLevelingTableMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        // texture size
        this.imageWidth = 248;
        this.imageHeight = 220;
        // offset for player inv title
        this.inventoryLabelY += 52;
        // offset container title
        this.titleLabelX -= 1;
    }

    @Override
    protected void init() {
        super.init();
        this.buttonList = new ButtonListWidget(this, this.leftPos + 104, this.topPos + 22, 136, 98);
        this.addWidget(this.buttonList);
    }

    @Override
    public void containerTick() {
        this.ticksSinceUpdate++;
        if (this.ticksSinceUpdate % 5 == 0) {
            this.ticksSinceUpdate = 0;
            this.buttonList.refreshList();
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack, 0); // render translucent grey background
        this.buttonList.render(poseStack, mouseX, mouseY, partialTicks);

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.0D, 10.0D);
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderPointsSummary(poseStack);
        poseStack.popPose();

        this.renderTooltip(poseStack, mouseX, mouseY); // render item tooltips
    }

    private void renderPointsSummary(PoseStack poseStack) {
        RenderSystem.enableDepthTest();
        String start = "container.toolleveling.tool_leveling_table.worth.";
        Component bonusPoints = Component.translatable(start + "bonus_points", String.format("%,d", this.menu.getBonusPoints()));
        Component invWorth = Component.translatable(start + "inv", String.format("%,d", this.menu.getContainerWorth()));
        float left = this.leftPos + 8;
        this.font.draw(poseStack, bonusPoints, left, topPos + 45, 4210752);
        this.font.draw(poseStack, invWorth, left, topPos + 56, 4210752);
        RenderSystem.disableDepthTest();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        this.buttonList.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        this.buttonList.mouseScrolled(mouseX, mouseY, delta);
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mX, int my) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        RenderSystem.enableDepthTest();
        blit(poseStack, this.leftPos, this.topPos, 0, 0, imageWidth, imageHeight, imageWidth, imageHeight);
        RenderSystem.disableDepthTest();
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

}
