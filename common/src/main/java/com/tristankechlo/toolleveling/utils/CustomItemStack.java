package com.tristankechlo.toolleveling.utils;

import com.google.common.collect.Lists;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class CustomItemStack {

    public static final CustomItemStack EMPTY = new CustomItemStack(Items.AIR, 0);
    public final ItemStack stack;
    public final long count;
    private final ResourceLocation tag;

    public CustomItemStack(Item stack, long count, ResourceLocation tag) {
        this.stack = stack.getDefaultInstance();
        this.count = count;
        this.tag = tag;
    }

    public CustomItemStack(Item stack, long count) {
        this(stack, count, null);
    }

    public boolean isEmpty() {
        return this.stack.isEmpty();
    }

    public List<Component> getTooltipLines(TooltipFlag flag) {
        List<Component> lines = Lists.newArrayList();
        Component hoverName = stack.getHoverName();
        MutableComponent title = new TextComponent("").append(hoverName).withStyle(stack.getRarity().color);
        if (stack.hasCustomHoverName()) {
            title.withStyle(ChatFormatting.ITALIC);
        }
        lines.add(title);

        // add item id
        if (flag.isAdvanced()) {
            lines.add(new TranslatableComponent(Registry.ITEM.getKey(this.stack.getItem()).toString()).withStyle(ChatFormatting.DARK_GRAY));
        }

        // add tag
        if (tag != null) {
            lines.add(new TextComponent("Item-Tag: #" + tag).withStyle(ChatFormatting.DARK_GRAY));
        }

        // add count
        lines.add(new TranslatableComponent("screen.toolleveling.item_value_worth", String.format("%,d", count)).withStyle(ChatFormatting.DARK_GRAY));
        return lines;
    }

}
