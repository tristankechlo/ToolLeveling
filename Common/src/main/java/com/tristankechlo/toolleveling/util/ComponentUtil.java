package com.tristankechlo.toolleveling.util;

import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.function.Supplier;

public final class ComponentUtil {

    public static MutableComponent make(String string) {
        return Component.translatable("screen.toolleveling.tool_leveling_table" + string);
    }

    public static MutableComponent makeTitle(String str) {
        return make(str).withStyle(ChatFormatting.GOLD, ChatFormatting.UNDERLINE);
    }

    public static Component makePercentage(ToolLevelingTableMenu.PercentageHolder p) {
        return makePercentage(p.enchantment.getDescriptionId(), p.percentage);
    }

    public static Component makePercentage(String str, float percentage) {
        percentage = Math.round(percentage * 10000F) / 100F; // round to 2 decimal place
        MutableComponent text = Component.translatable(str).withStyle(ChatFormatting.GRAY);
        text.append(Component.literal(" " + percentage + "%").withStyle(ChatFormatting.GREEN));
        return text;
    }

    public static MutableComponent makeChance(String str, Supplier<Float> chance) {
        String start = "screen.toolleveling.tool_leveling_table.";
        return Component.translatable(start + str, chance.get()).withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC).append("%");
    }

    public static Component makeBonus(String str, int number) {
        MutableComponent text = Component.translatable(str).withStyle(ChatFormatting.GRAY);
        text.append(Component.literal(" " + number).withStyle(ChatFormatting.GREEN));
        return text;
    }

}
