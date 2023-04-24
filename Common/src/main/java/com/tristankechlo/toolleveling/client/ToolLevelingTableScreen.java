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
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.List;

public class ToolLevelingTableScreen extends AbstractContainerScreen<ToolLevelingTableMenu> {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation(ToolLeveling.MOD_ID, "textures/gui/tool_leveling_table.png");
    private static boolean shouldRenderPercentages = false;
    List<Tuple<Enchantment, Float>> percentages;
    private float successChance;
    private byte ticksSinceUpdate = 0;
    private Button infoButton;

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
        this.percentages = this.getMenu().getPercentages();
        this.successChance = Util.getSuccessChance(this.getMenu());
        Button.OnPress action = (button) -> {
            shouldRenderPercentages = !shouldRenderPercentages;
        };
        this.infoButton = this.addRenderableWidget(new Button.Builder(Component.literal("?"), action)
                .pos(this.leftPos + this.imageWidth - 20, this.topPos + 4).size(16, 16)
                .tooltip(Tooltip.create(Component.literal("This is a test"))).build());
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
            percentages = this.getMenu().getPercentages();
            percentages.sort((o1, o2) -> Float.compare(o2.getB(), o1.getB()));
            this.successChance = Util.getSuccessChance(this.getMenu());
        }
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(poseStack); // render translucent grey background
        super.render(poseStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(poseStack, mouseX, mouseY); // render tooltips if hovered

        if (!shouldRenderPercentages || this.percentages.isEmpty()) {
            return;
        }

        int x = this.leftPos + this.imageWidth + 7;
        int y = this.topPos + 7;
        for (int i = 0; i < this.percentages.size(); i++) {
            MutableComponent text = createComponent(this.percentages.get(i));
            drawString(poseStack, this.font, text, x, y + (i * 11), 0xFFFFFF);
        }

        MutableComponent text = Component.literal("Success Chance: ").withStyle(ChatFormatting.GRAY);
        text.append(Component.literal(this.successChance + "%").withStyle(ChatFormatting.WHITE));
        drawString(poseStack, this.font, text, x, y + (this.percentages.size() * 11) + 4, 0xFFFFFF);
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mX, int my) {
        RenderSystem.setShaderTexture(0, GUI_TEXTURE);
        blit(poseStack, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        if (!shouldRenderPercentages || this.percentages.isEmpty()) {
            return;
        }

        int x = this.leftPos + this.imageWidth;
        int y = this.topPos + 6;
        blit(poseStack, x, y - 6, 0, 224, 120, 6);
        for (int i = 0; i < this.percentages.size(); i++) {
            blit(poseStack, x, y + (i * 11), 0, 228, 120, 12);
        }
        blit(poseStack, x, y + (this.percentages.size() * 11), 0, 252, 120, 4);
    }

    private static MutableComponent createComponent(Tuple<Enchantment, Float> tuple) {
        Enchantment enchantment = tuple.getA();
        float percentage = tuple.getB();
        percentage = Math.round(percentage * 1000F) / 10F; // round to 1 decimal place
        MutableComponent text = Component.translatable(enchantment.getDescriptionId()).withStyle(ChatFormatting.GRAY);
        text.append(Component.literal(" " + percentage + "%").withStyle(ChatFormatting.GREEN));
        return text;
    }

    @Override
    public boolean mouseClicked(double $$0, double $$1, int $$2) {
        if (!this.infoButton.isMouseOver($$0, $$1)) {
            this.infoButton.setFocused(false); // unfocus button if clicked outside of it
        }
        return super.mouseClicked($$0, $$1, $$2);
    }

}
