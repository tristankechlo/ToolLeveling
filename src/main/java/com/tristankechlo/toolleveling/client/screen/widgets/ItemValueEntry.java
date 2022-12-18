package com.tristankechlo.toolleveling.client.screen.widgets;

import com.google.common.collect.ImmutableList;
import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;

import java.util.List;

@Environment(EnvType.CLIENT)
public class ItemValueEntry extends ElementListWidget.Entry<ItemValueEntry> {

    private final ItemValueScreen screen;
    private final DefaultedList<Pair<ItemStack, Long>> list;

    public ItemValueEntry(ItemValueScreen screen, DefaultedList<Pair<ItemStack, Long>> list) {
        this.screen = screen;
        if (list.size() != ItemValuesListWidget.ROW_SIZE) {
            throw new IllegalArgumentException("the size of the list needs to be " + ItemValuesListWidget.ROW_SIZE);
        }
        this.list = list;
    }

    @Override
    public void render(MatrixStack mStack, int index, int top, int left, int entryWidth, int entryHeight, int mouseX,
                       int mouseY, boolean isMouseOver, float partialTicks) {

        int indexWidth = entryWidth / ItemValuesListWidget.ROW_SIZE;
        for (int i = 0; i < list.size(); i++) {
            int x = left + (i * indexWidth);
            int y = top + 3;
            Pair<ItemStack, Long> tuple = list.get(i);
            screen.getClient().getItemRenderer().renderGuiItemIcon(tuple.getLeft(), x, y);
            if (isMouseOverItem(x, y, mouseX, mouseY)) {
                this.renderItemTooltip(mStack, tuple.getLeft(), mouseX, mouseY, tuple.getRight());
            }
        }
    }

    private void renderItemTooltip(MatrixStack mStack, ItemStack iStack, int mouseX, int mouseY, long worth) {
        List<Text> tooltips = screen.getTooltipFromItem(iStack);
        tooltips.add(new TranslatableText("screen.toolleveling.item_value_worth", worth).formatted(Formatting.DARK_GRAY));
        screen.renderTooltip(mStack, tooltips, mouseX, mouseY);
    }

    private boolean isMouseOverItem(int left, int top, int mouseX, int mouseY) {
        return mouseX > left && mouseX <= (left + 16) && mouseY > top && mouseY <= (top + 16);
    }

    @Override
    public List<? extends Element> children() {
        return ImmutableList.of();
    }

    @Override
    public List<? extends Selectable> selectableChildren() {
        return ImmutableList.of();
    }

}
