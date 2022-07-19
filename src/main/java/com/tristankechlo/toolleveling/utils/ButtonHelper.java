package com.tristankechlo.toolleveling.utils;

import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableHandledScreen;
import com.tristankechlo.toolleveling.client.screen.widgets.ButtonEntry;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

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
        } else if (entry.status == ButtonStatus.BLACKLISTED || entry.status == ButtonStatus.CAPPED
                || entry.status == ButtonStatus.MAX_LEVEL || entry.status == ButtonStatus.MIN_LEVEL) {
            return false;
        }
        return false;
    }

    public static ButtonEntry getButtonEntry(ToolLevelingTableHandledScreen parent, Enchantment enchantment, int level) {
        List<Enchantment> whitelist = ToolLevelingConfig.whitelist.getValue();
        List<Enchantment> blacklist = ToolLevelingConfig.blacklist.getValue();
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
        // check if the enchantment is over the set minimum level
        else if (!Utils.isEnchantmentOverMinimum(enchantment, level)) {
            buttonEntry.status = ButtonStatus.MIN_LEVEL;
        }
        // check if the enchantment is allowed to level up
        // determinated by the config enchantmentCaps
        else if (Utils.isEnchantmentAtCap(enchantment, level)) {
            buttonEntry.status = ButtonStatus.CAPPED;
        }
        // although the level is defined as an integer, the actual maximum is a short
        // a higher enchantment level than a short will result in a negative level
        else if (level >= Short.MAX_VALUE) {
            buttonEntry.status = ButtonStatus.MAX_LEVEL;
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

    public static Text getButtonText(ButtonEntry entry) {
        return Text.translatable(entry.name).formatted(getButtonTextFormatting(entry));
    }

    public static List<Text> getButtonToolTips(ButtonEntry data) {
        List<Text> tooltip = new ArrayList<>();
        tooltip.add(Text.translatable(data.name).formatted(Formatting.AQUA));
        final String start = "container.toolleveling.tool_leveling_table";
        if (ButtonHelper.shouldButtonBeActive(data) || Utils.freeCreativeUpgrades(MinecraftClient.getInstance().player)) {
            tooltip.add(Text.translatable(start + ".current_level", data.currentLevel).formatted(Formatting.DARK_GRAY));
            tooltip.add(Text.translatable(start + ".next_level", (data.currentLevel + 1)).formatted(Formatting.DARK_GRAY));
            tooltip.add(Text.translatable(start + ".cost", data.upgradeCost).formatted(Formatting.DARK_GRAY));
        }
        if (Utils.freeCreativeUpgrades(MinecraftClient.getInstance().player)) {
            tooltip.add(Text.translatable(start + ".free_creative").formatted(Formatting.GREEN));
        } else if (data.status != ButtonStatus.NORMAL) {
            tooltip.add(Text.translatable(start + ".error." + data.status.toString().toLowerCase()).formatted(ButtonHelper.getButtonTextFormatting(data)));
        }
        return tooltip;
    }

    public static Formatting getButtonTextFormatting(ButtonEntry entry) {
        Formatting format = Formatting.RESET;
        if (Utils.freeCreativeUpgrades(MinecraftClient.getInstance().player)) {
            return Formatting.RESET;
        }
        if (entry.status != ButtonStatus.NORMAL) {
            format = Formatting.DARK_RED;
        }
        if (entry.status == ButtonStatus.USELESS) {
            format = Formatting.YELLOW;
        }
        return format;
    }

    public enum ButtonStatus {
        NORMAL, // nothing special, can be leveled
        BLACKLISTED, // enchantment is blacklisted, or not on the whitelist
        USELESS, // leveling this enchantment will have no effect
        BREAK, // enchantment will break when leveled higher
        MAX_LEVEL, // enchantment is at the possible maximum level (Short.MAX_VALUE)
        CAPPED, // enchantment is at the maximum level (enchantmentCaps)
        MIN_LEVEL // enchantment is not over the set minimum level (minimumEnchantmentLevel)
    }
}
