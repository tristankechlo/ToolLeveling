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
        ButtonStatus status = entry.getStatus();
        if (status == ButtonStatus.NORMAL) {
            return true;
        } else if (status == ButtonStatus.USELESS) {
            return ToolLevelingConfig.allowLevelingUselessEnchantments.getValue();
        } else if (status == ButtonStatus.BREAK) {
            return ToolLevelingConfig.allowLevelingBreakingEnchantments.getValue();
        } else if (status == ButtonStatus.NOT_WHITELISTED ||status == ButtonStatus.BLACKLISTED || status == ButtonStatus.CAPPED
                || status == ButtonStatus.MAX_LEVEL || status == ButtonStatus.MIN_LEVEL) {
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
            buttonEntry.setStatus(ButtonStatus.NOT_WHITELISTED);
            return buttonEntry;
        }
        // only list enchantments that are not on the blacklist
        if (whitelist.isEmpty() && blacklist.contains(enchantment)) {
            buttonEntry.setStatus(ButtonStatus.BLACKLISTED);
            return buttonEntry;
        }
        // although the level is defined as an integer, the actual maximum is a short
        // a higher enchantment level than a short will result in a negative level
        if (level >= Short.MAX_VALUE) {
            buttonEntry.setStatus(ButtonStatus.MAX_LEVEL);
            return buttonEntry;
        }
        // check if the enchantment is allowed to level up
        // determinated by the config enchantmentCaps
        if (Utils.isEnchantmentAtCap(enchantment, level)) {
            buttonEntry.setStatus(ButtonStatus.CAPPED);
            return buttonEntry;
        }
        // check if the enchantment is over the set minimum level
        if (!Utils.isEnchantmentOverMinimum(enchantment, level)) {
            buttonEntry.setStatus(ButtonStatus.MIN_LEVEL);
            return buttonEntry;
        }
        // leveling these enchantments will do absolutely nothing
        if (enchantment.getMaxLevel() == 1) {
            buttonEntry.setStatus(ButtonStatus.USELESS);
            return buttonEntry;
        }
        // check if the enchantment can still be leveled
        // some enchantments will break when leveled to high
        if (Utils.willEnchantmentBreak(enchantment, level)) {
            buttonEntry.setStatus(ButtonStatus.BREAK);
            return buttonEntry;
        }
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
        } else if (data.getStatus() != ButtonStatus.NORMAL) {
            tooltip.add(Text.translatable(start + ".error." + data.getStatus().toString().toLowerCase()).formatted(ButtonHelper.getButtonTextFormatting(data)));
        }
        return tooltip;
    }

    public static Formatting getButtonTextFormatting(ButtonEntry entry) {
        Formatting format = Formatting.RESET;
        if (Utils.freeCreativeUpgrades(MinecraftClient.getInstance().player)) {
            return Formatting.RESET;
        }
        if (entry.getStatus() != ButtonStatus.NORMAL) {
            format = Formatting.DARK_RED;
        }
        if (entry.getStatus() == ButtonStatus.USELESS) {
            format = Formatting.YELLOW;
        }
        return format;
    }

    public enum ButtonStatus {
        NORMAL, // nothing special, can be leveled
        NOT_WHITELISTED, // not on the whitelist
        BLACKLISTED, // enchantment is blacklisted
        USELESS, // leveling this enchantment will have no effect
        BREAK, // enchantment will break when leveled higher
        MAX_LEVEL, // enchantment is at the possible maximum level (Short.MAX_VALUE)
        CAPPED, // enchantment is at the maximum level (enchantmentCaps)
        MIN_LEVEL // enchantment is not over the set minimum level (minimumEnchantmentLevel)
    }
}
