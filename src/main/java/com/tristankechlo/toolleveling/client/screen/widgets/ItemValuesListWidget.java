package com.tristankechlo.toolleveling.client.screen.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;
import com.tristankechlo.toolleveling.config.ItemValues;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.widget.ElementListWidget;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;

@Environment(EnvType.CLIENT)
public class ItemValuesListWidget extends ElementListWidget<ItemValueEntry> {

	public static final int ROW_HEIGHT = 26;
	public static final int ROW_SIZE = 7;
	private final ItemValueScreen screen;
	private final int listWidth;

	public ItemValuesListWidget(ItemValueScreen screen, int listWidth, int top, int bottom) {
		super(screen.getClient(), listWidth, screen.height, top, bottom, ROW_HEIGHT);
		this.screen = screen;
		this.listWidth = listWidth;

		// disable rendering of the dirt background
		this.setRenderBackground(false);
		this.setRenderHorizontalShadows(false);
		this.setRenderHeader(false, 0);

		this.refreshList();
	}

	private void refreshList() {
		List<Pair<ItemStack, Long>> entries = ItemValues.itemValues.entrySet().stream()
				.sorted((o1, o2) -> o2.getValue().compareTo(o1.getValue()))
				.map((entry) -> new Pair<>(new ItemStack(entry.getKey()), entry.getValue()))
				.collect(Collectors.toList());

		// split into n smaller lists
		int partitionSize = ItemValuesListWidget.ROW_SIZE;
		List<List<Pair<ItemStack, Long>>> partitions = new ArrayList<>();
		for (int i = 0; i < entries.size(); i += partitionSize) {
			partitions.add(entries.subList(i, Math.min(i + partitionSize, entries.size())));
		}
		// transform to nonnull lists and add as entry
		for (List<Pair<ItemStack, Long>> list : partitions) {
			DefaultedList<Pair<ItemStack, Long>> nonNullList = DefaultedList.ofSize(partitionSize,
					new Pair<>(ItemStack.EMPTY, 0L));
			for (int i = 0; i < list.size(); i++) {
				nonNullList.set(i, list.get(i));
			}
			this.addEntry(new ItemValueEntry(screen, nonNullList));
		}
	}

	@Override
	protected int getScrollbarPositionX() {
		return this.right - 10;
	}

	@Override
	public int getRowWidth() {
		return this.listWidth;
	}

}
