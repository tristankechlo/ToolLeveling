package com.tristankechlo.toolleveling.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import com.tristankechlo.toolleveling.network.NetworkHelper;
import com.tristankechlo.toolleveling.util.ComponentUtil;
import com.tristankechlo.toolleveling.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.stream.Collectors;

public class ToolLevelingTableScreen extends AbstractContainerScreen<ToolLevelingTableMenu> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ToolLeveling.MOD_ID, "textures/gui/tool_leveling_table.png");
    private static final Tooltip TOOLTIP_PERCENTAGES = Tooltip.create(ComponentUtil.make(".tooltip.percentages"));
    private static final Tooltip TOOLTIP_HELP = Tooltip.create(ComponentUtil.make(".tooltip.help"));
    private static final Component PERCENTAGES_TITLE = ComponentUtil.makeTitle(".title.percentages");
    private static final Component SUCCESS_CHANCE_TITLE = ComponentUtil.makeTitle(".title.success_chance");
    private static final Component BONUS_TITLE = ComponentUtil.makeTitle(".title.bonuses");
    private static final Component HELP = ComponentUtil.make(".help").withStyle(ChatFormatting.GRAY);
    private static boolean shouldRenderPercentages = false;
    private static boolean shouldRenderHelp = false;
    private final InfoFieldRenderer percentagesField = new InfoFieldRenderer(0xD9080808, 0xFF8c045f, 0xFFD82FA0);
    private final InfoFieldRenderer successChanceField = new InfoFieldRenderer(0xD9080808, 0xFF3B51BF, 0xFF4F80FF);
    private final InfoFieldRenderer bonusItemField = new InfoFieldRenderer(0xD9080808, 0xFF007F0E, 0xFF00CC17);
    private final InfoFieldRenderer helpField = new InfoFieldRenderer(0xFF212121, 0xFF000000, 0xFF555555);
    private Component minChanceText;
    private Component maxChanceText;
    private byte ticksSinceUpdate = 0;

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
        // button percentages
        this.addRenderableWidget(new Button.Builder(Component.literal("%"), (b) -> shouldRenderPercentages = !shouldRenderPercentages)
                .pos(this.leftPos + this.imageWidth - 18, this.topPos + 94).size(14, 14)
                .tooltip(TOOLTIP_PERCENTAGES).build());
        // button help
        this.addRenderableWidget(new Button.Builder(Component.literal("?"), (b) -> shouldRenderHelp = !shouldRenderHelp)
                .pos(this.leftPos + this.imageWidth - 76, this.topPos + 94).size(14, 14)
                .tooltip(TOOLTIP_HELP).build());
        // button start upgrade process
        this.addRenderableWidget(new Button.Builder(Component.literal("start"), (b) -> {
            NetworkHelper.INSTANCE.startUpgradeProcess(this.getMenu().getPos());
        }).pos(this.leftPos + imageWidth - 60, this.topPos + 94).size(40, 14).build());

        this.percentagesField.setSpaceAfterTitle(true);
        this.successChanceField.setSpaceAfterTitle(true);
        this.bonusItemField.setSpaceAfterTitle(true);

        this.minChanceText = ComponentUtil.makeChance("min_success_chance", ToolLevelingConfig.minSuccessChance);
        this.maxChanceText = ComponentUtil.makeChance("max_success_chance", ToolLevelingConfig.maxSuccessChance);

        this.helpField.setLines(List.of(HELP), font, this.leftPos - 10);
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
            var components = percentages.stream().map(ComponentUtil::makePercentage).collect(Collectors.toList());
            components.add(0, PERCENTAGES_TITLE);
            this.percentagesField.setLines(components);

            // update success chance
            float successChance = Util.getSuccessChance(this.getMenu());
            Component chanceText = ComponentUtil.makePercentage("screen.toolleveling.tool_leveling_table.success_chance", successChance);
            this.successChanceField.setLines(List.of(SUCCESS_CHANCE_TITLE, chanceText, minChanceText, maxChanceText));

            // update bonus items
            var cycles = this.getMenu().getCycles();
            Component bonusItemText = ComponentUtil.makeBonus("screen.toolleveling.tool_leveling_table.iterations", cycles);
            var levels = this.getMenu().getLevels();
            Component bonusLevelText = ComponentUtil.makeBonus("screen.toolleveling.tool_leveling_table.strength", levels);
            this.bonusItemField.setLines(List.of(BONUS_TITLE, bonusItemText, bonusLevelText));
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
                this.percentagesField.render(poseStack, this.font, x, y, fieldWidth);
                y += this.percentagesField.calcHeight() + 1;
            }
            this.successChanceField.render(poseStack, this.font, x, y, fieldWidth);
            y += this.successChanceField.calcHeight() + 1;
            this.bonusItemField.render(poseStack, this.font, x, y, fieldWidth);
        }

        if (shouldRenderHelp) {
            int x = this.leftPos - this.helpField.calcWidth(this.font);
            int y = this.topPos;
            this.helpField.render(poseStack, this.font, x, y);
        }
    }

    private int calcFieldWidth() {
        int width1 = this.percentagesField.calcWidth(this.font);
        int width2 = this.successChanceField.calcWidth(this.font);
        int width3 = this.bonusItemField.calcWidth(this.font);
        int targetWidth = Math.max(width1, Math.max(width2, width3)); // get the widest field
        int widthFree = this.width - this.leftPos - this.imageWidth - 3; // get the free space on the right side
        targetWidth = Math.min(targetWidth, widthFree); // limit width to screen width
        return targetWidth;
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mX, int my) {
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (var widget : this.children()) {
            if (!widget.isMouseOver(mouseX, mouseY)) {
                widget.setFocused(false);   // unfocus all widgets if clicked outside of them
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

}
