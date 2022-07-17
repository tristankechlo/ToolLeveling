package com.tristankechlo.toolleveling.utils;

import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.client.screen.widgets.ButtonEntry;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;

import java.util.ArrayList;
import java.util.List;

public final class ButtonHelper {

    public static boolean shouldButtonBeActive(ButtonEntry entry) {
        if (entry.status == ButtonStatus.NORMAL) {
            return true;
        } else if (entry.status == ButtonStatus.USELESS) {
            return ToolLevelingConfig.allowLevelingUselessEnchantments.getValue();
        } else if (entry.status == ButtonStatus.BREAK) {
            return ToolLevelingConfig.allowLevelingBreakingEnchantments.getValue();
        } else if (entry.status == ButtonStatus.BLACKLISTED || entry.status == ButtonStatus.CAPPED || entry.status == ButtonStatus.MAXLEVEL) {
            return false;
        }
        return false;
    }

    public static ButtonEntry getButtonEntry(ToolLevelingTableScreen parent, Enchantment enchantment, int level) {
        List<Enchantment> whitelist = ToolLevelingConfig.enchantmentWhitelist.getValue();
        List<Enchantment> blacklist = ToolLevelingConfig.enchantmentBlacklist.getValue();
        ButtonEntry buttonEntry = new ButtonEntry(parent, enchantment, level);

        // if whitelist is not empty, mark all enchantments as blacklisted if they are
        // not on the whitelist
        if (!whitelist.isEmpty() && !whitelist.contains(enchantment)) {
            buttonEntry.status = ButtonStatus.BLACKLISTED;
        }
        // only list enchantments that are not on the blacklist
        else if (whitelist.isEmpty() && blacklist.contains(enchantment)) {
            buttonEntry.status = ButtonStatus.BLACKLISTED;
        }
        // check if the enchantment is allowed to level up
        // determinated by the config enchantmentCaps
        else if (Utils.isEnchantmentAtCap(enchantment, level)) {
            buttonEntry.status = ButtonStatus.CAPPED;
        }
        // although the level is defined as an integer, the actual maximum is a short
        // a higher enchantment level than a short will result in a negative level
        else if (level >= Short.MAX_VALUE) {
            buttonEntry.status = ButtonStatus.MAXLEVEL;
        }
        // leveling these enchantments will do absolutely nothing
        else if (enchantment.getMaxLevel() == 1) {
            buttonEntry.status = ButtonStatus.USELESS;
        }
        // check if the enchantment can still be leveled
        // some enchantments will break when leveled to high
        else if (Utils.willEnchantmentBreak(enchantment, level)) {
            buttonEntry.status = ButtonStatus.BREAK;
        }
        buttonEntry.updateButtonText();
        return buttonEntry;
    }

    public static Component getButtonText(ButtonEntry entry) {
        return Component.translatable(entry.name).withStyle(getButtonTextFormatting(entry));
    }

    public static List<Component> getButtonToolTips(ButtonEntry data) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable(data.name).withStyle(ChatFormatting.AQUA));
        final String start = "container.toolleveling.tool_leveling_table";
        if (ButtonHelper.shouldButtonBeActive(data) || Utils.freeCreativeUpgrades(Minecraft.getInstance().player)) {
            tooltip.add(Component.translatable(start + ".current_level", data.currentLevel).withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.translatable(start + ".next_level", (data.currentLevel + 1)).withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.translatable(start + ".cost", data.upgradeCost).withStyle(ChatFormatting.DARK_GRAY));
        }
        if (Utils.freeCreativeUpgrades(Minecraft.getInstance().player)) {
            tooltip.add(Component.translatable(start + ".free_creative").withStyle(ChatFormatting.GREEN));
        } else if (data.status != ButtonStatus.NORMAL) {
            tooltip.add(Component.translatable(start + ".error." + data.status.toString().toLowerCase()).withStyle(ButtonHelper.getButtonTextFormatting(data)));
        }
        return tooltip;
    }

    public static ChatFormatting getButtonTextFormatting(ButtonEntry entry) {
        ChatFormatting format = ChatFormatting.RESET;
        if (Utils.freeCreativeUpgrades(Minecraft.getInstance().player)) {
            return ChatFormatting.RESET;
        }
        if (entry.status != ButtonStatus.NORMAL) {
            format = ChatFormatting.DARK_RED;
        }
        if (entry.status == ButtonStatus.USELESS) {
            format = ChatFormatting.YELLOW;
        }
        return format;
    }

    public enum ButtonStatus {
        NORMAL,
        BLACKLISTED,
        USELESS,
        MAXLEVEL,
        BREAK,
        CAPPED;
    }
}
