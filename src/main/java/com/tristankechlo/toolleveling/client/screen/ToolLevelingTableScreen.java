package com.tristankechlo.toolleveling.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.tristankechlo.toolleveling.client.screen.widgets.ButtonEntry;
import com.tristankechlo.toolleveling.client.screen.widgets.ButtonListWidget;
import com.tristankechlo.toolleveling.container.ToolLevelingTableContainer;
import com.tristankechlo.toolleveling.utils.ButtonHelper;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class ToolLevelingTableScreen extends ContainerScreen<ToolLevelingTableContainer> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(Names.MOD_ID, "textures/gui/tool_leveling_table.png");
    protected ButtonListWidget buttonList;
    private byte ticksSinceUpdate = 0;

    public ToolLevelingTableScreen(ToolLevelingTableContainer container, PlayerInventory inv, ITextComponent name) {
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
        this.buttonList = new ButtonListWidget(this, 136, this.topPos + 23, this.topPos + 118);
        this.buttonList.setLeftPos(this.leftPos + 105);
        this.children.add(buttonList);

    }

    @Override
    public void tick() {
        super.tick();
        this.ticksSinceUpdate++;
        if (this.ticksSinceUpdate % 5 == 0) {
            this.ticksSinceUpdate = 0;
            this.buttonList.refreshList();
        }
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack); // render translucent grey background
        this.buttonList.render(matrixStack, mouseX, mouseY, partialTicks); // render button scroll view
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY); // render item toolltips

        // render button tooltips
        for (int i = 0; i < this.buttonList.children().size(); i++) {
            ButtonEntry entry = this.buttonList.children().get(i);
            if (entry.button.isHovered()) {
                List<ITextComponent> tooltip = ButtonHelper.getButtonToolTips(entry);
                this.renderComponentTooltip(matrixStack, tooltip, mouseX, mouseY);
            }
        }

        this.renderPointsSummary(matrixStack);
    }

    private void renderPointsSummary(MatrixStack stack) {
        String start = "container.toolleveling.tool_leveling_table.worth.";
        ITextComponent bonusPoints = new TranslationTextComponent(start + "bonus_points", this.menu.getBonusPoints());
        ITextComponent invWorth = new TranslationTextComponent(start + "inv", this.menu.getContainerWorth());
        float left = this.leftPos + 8;
        this.font.draw(stack, bonusPoints, left, topPos + 45, 4210752);
        this.font.draw(stack, invWorth, left, topPos + 56, 4210752);
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

    @SuppressWarnings("deprecation")
    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mX, int mY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bind(GUI_TEXTURE);
        blit(matrixStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, this.imageWidth, this.imageHeight);
    }

}
