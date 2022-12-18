package com.tristankechlo.toolleveling.client.screen.widgets;

import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;
import com.tristankechlo.toolleveling.config.ItemValueConfig;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@OnlyIn(Dist.CLIENT)
public class ItemValuesListWidget extends ExtendedList<ItemValueEntry> {

    public static final int ROW_HEIGHT = 26;
    public static final int ROW_SIZE = 7;
    private final ItemValueScreen screen;
    private final int listWidth;

    public ItemValuesListWidget(ItemValueScreen screen, int listWidth, int top, int bottom) {
        super(screen.getMinecraft(), listWidth, screen.height, top, bottom, ROW_HEIGHT);
        this.screen = screen;
        this.listWidth = listWidth;

        // disable rendering of the dirt background
        this.setRenderBackground(false);
        this.setRenderTopAndBottom(false);
        this.setRenderHeader(false, 0);

        this.refreshList();
    }

    private void refreshList() {
        List<Tuple<ItemStack, Long>> entries = ItemValueConfig.itemValues.getValue().entrySet().stream()
                .sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
                .map((entry) -> new Tuple<>(new ItemStack(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());

        // split into n smaller lists
        int partitionSize = ItemValuesListWidget.ROW_SIZE;
        List<List<Tuple<ItemStack, Long>>> partitions = new ArrayList<>();
        for (int i = 0; i < entries.size(); i += partitionSize) {
            partitions.add(entries.subList(i, Math.min(i + partitionSize, entries.size())));
        }
        // transform to nonnull lists and add as entry
        for (List<Tuple<ItemStack, Long>> list : partitions) {
            NonNullList<Tuple<ItemStack, Long>> nonNullList = NonNullList.withSize(partitionSize, new Tuple<>(ItemStack.EMPTY, 0L));
            for (int i = 0; i < list.size(); i++) {
                nonNullList.set(i, list.get(i));
            }
            this.addEntry(new ItemValueEntry(screen, nonNullList));
        }
    }

    @Override
    protected int getScrollbarPosition() {
        return this.x1 - 10;
    }

    @Override
    public int getRowWidth() {
        return this.listWidth;
    }

}
