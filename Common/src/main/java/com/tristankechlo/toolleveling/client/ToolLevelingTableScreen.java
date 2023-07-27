package com.tristankechlo.toolleveling.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import com.tristankechlo.toolleveling.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.stream.Collectors;

public class ToolLevelingTableScreen extends AbstractContainerScreen<ToolLevelingTableMenu> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ToolLeveling.MOD_ID, "textures/gui/tool_leveling_table.png");
    private static final Tooltip TOOLTIP_PERCENTAGES = Tooltip.create(make(".tooltip.percentages"));
    private static final Tooltip TOOLTIP_HELP = Tooltip.create(make(".tooltip.help"));
    private static final Component PERCENTAGES_TITLE = make(".percentages.title").withStyle(ChatFormatting.GRAY);
    private static boolean shouldRenderPercentages = false;
    private static boolean shouldRenderHelp = false;
    private final InfoFieldRenderer percentagesInfoField = new InfoFieldRenderer(0xFF212121, 0xFF7C0053, 0xFFD82FA0);
    private final InfoFieldRenderer successChanceField = new InfoFieldRenderer(0xFF212121, 0xFF3B51BF, 0xFF4F80FF);
    private byte ticksSinceUpdate = 0;
    private Button infoButton;
    private Button helpButton;

    public ToolLevelingTableScreen(ToolLevelingTableMenu container, Inventory inv, Component name) {
        super(container, inv, name);
        this.imageWidth = 176;
        this.imageHeight = 194;
        this.inventoryLabelY += 29;
        this.titleLabelX -= 2;
    }

    @Override
    protected void init() {
        super.init();
        this.infoButton = this.addRenderableWidget(new Button.Builder(Component.literal("%"), (b) -> shouldRenderPercentages = !shouldRenderPercentages)
                .pos(this.leftPos + this.imageWidth - 34, this.topPos + 94).size(14, 14)
                .tooltip(TOOLTIP_PERCENTAGES).build());
        this.helpButton = this.addRenderableWidget(new Button.Builder(Component.literal("?"), (b) -> shouldRenderHelp = !shouldRenderHelp)
                .pos(this.leftPos + this.imageWidth - 18, this.topPos + 94).size(14, 14)
                .tooltip(TOOLTIP_HELP).build());

        this.percentagesInfoField.setSpaceAfterTitle(true);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (!shouldRenderPercentages) {
            return;
        }
        this.ticksSinceUpdate++;
        if (this.ticksSinceUpdate % 5 == 0) {
            this.ticksSinceUpdate = 0;

            // update percentages
            var percentages = this.getMenu().getPercentages();
            percentages.sort((o1, o2) -> Float.compare(o2.getB(), o1.getB()));
            var components = percentages.stream().map(ToolLevelingTableScreen::createComponent).collect(Collectors.toList());
            components.add(0, PERCENTAGES_TITLE);
            this.percentagesInfoField.setLines(components);

            // update success chance
            float successChance = Util.getSuccessChance(this.getMenu());
            Component chanceText = createComponent(new Tuple<>("screen.toolleveling.tool_leveling_table.success_chance", successChance));
            this.successChanceField.setLines(List.of(chanceText));
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack); // render translucent grey background
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY); // render tooltips if hovered

        if (shouldRenderPercentages) {
            int x = this.leftPos + this.imageWidth;
            int y = this.topPos;
            int fieldWidth = this.calcFieldWidth();
            if (this.getMenu().hasAnyBooks()) {
                this.percentagesInfoField.render(poseStack, this.font, x, y, fieldWidth);
                y += this.percentagesInfoField.calcHeight() + 1;
            }
            this.successChanceField.render(poseStack, this.font, x, y, fieldWidth);
        }
    }

    private int calcFieldWidth() {
        int width1 = this.percentagesInfoField.calcWidth(this.font);
        int width2 = this.successChanceField.calcWidth(this.font);
        int targetWidth = Math.max(width1, width2); // get the widest field
        int widthFree = this.width - this.leftPos - this.imageWidth - 3; // get the free space on the right side
        targetWidth = Math.min(targetWidth, widthFree); // limit width to screen width
        return targetWidth;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mX, int my) {
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    private static Component createComponent(Tuple<String, Float> tuple) {
        float percentage = tuple.getB();
        percentage = Math.round(percentage * 10000F) / 100F; // round to 2 decimal place
        MutableComponent text = Component.translatable(tuple.getA()).withStyle(ChatFormatting.GRAY);
        text.append(Component.literal(" " + percentage + "%").withStyle(ChatFormatting.GREEN));
        return text;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!this.infoButton.isMouseOver(mouseX, mouseY)) {
            this.infoButton.setFocused(false); // unfocus button if clicked outside of it
        }
        if (!this.helpButton.isMouseOver(mouseX, mouseY)) {
            this.helpButton.setFocused(false); // unfocus button if clicked outside of it
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private static MutableComponent make(String string) {
        return Component.translatable("screen.toolleveling.tool_leveling_table" + string);
    }

}
