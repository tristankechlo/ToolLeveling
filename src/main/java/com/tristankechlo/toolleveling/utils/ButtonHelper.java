package com.tristankechlo.toolleveling.utils;

import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.client.screen.widgets.ButtonEntry;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.enchantment.Enchantment;

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
        } else if (status == ButtonStatus.NOT_WHITELISTED || status == ButtonStatus.BLACKLISTED || status == ButtonStatus.CAPPED
                || status == ButtonStatus.MAX_LEVEL || status == ButtonStatus.MIN_LEVEL) {
            return false;
        }
        return false;
    }

    public static ButtonEntry getButtonEntry(ToolLevelingTableScreen parent, Enchantment enchantment, int level) {
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

    public static Component getButtonText(ButtonEntry entry) {
        return new TranslatableComponent(entry.name).withStyle(getButtonTextFormatting(entry));
    }

    public static List<Component> getButtonToolTips(ButtonEntry data) {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(new TranslatableComponent(data.name).withStyle(ChatFormatting.AQUA));
        final String start = "container.toolleveling.tool_leveling_table";
        if (ButtonHelper.shouldButtonBeActive(data) || Utils.freeCreativeUpgrades(Minecraft.getInstance().player)) {
            tooltip.add(new TranslatableComponent(start + ".current_level", data.currentLevel).withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(new TranslatableComponent(start + ".next_level", (data.currentLevel + 1)).withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(new TranslatableComponent(start + ".cost", data.upgradeCost).withStyle(ChatFormatting.DARK_GRAY));
        }
        if (Utils.freeCreativeUpgrades(Minecraft.getInstance().player)) {
            tooltip.add(new TranslatableComponent(start + ".free_creative").withStyle(ChatFormatting.GREEN));
        } else if (data.getStatus() != ButtonStatus.NORMAL) {
            tooltip.add(new TranslatableComponent(start + ".error." + data.getStatus().toString().toLowerCase()).withStyle(ButtonHelper.getButtonTextFormatting(data)));
        }
        return tooltip;
    }

    public static ChatFormatting getButtonTextFormatting(ButtonEntry entry) {
        ChatFormatting format = ChatFormatting.RESET;
        if (Utils.freeCreativeUpgrades(Minecraft.getInstance().player)) {
            return ChatFormatting.RESET;
        }
        if (entry.getStatus() != ButtonStatus.NORMAL) {
            format = ChatFormatting.DARK_RED;
        }
        if (entry.getStatus() == ButtonStatus.USELESS) {
            format = ChatFormatting.YELLOW;
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
