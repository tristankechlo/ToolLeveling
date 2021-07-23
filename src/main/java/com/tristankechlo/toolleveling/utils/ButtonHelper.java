package com.tristankechlo.toolleveling.utils;

import java.util.ArrayList;
import java.util.List;

import com.tristankechlo.toolleveling.client.screen.ButtonEntry;
import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.enchantment.Enchantment;

public class ButtonHelper {

	public static boolean shouldButtonBeActive(ButtonEntry entry) {
		if (entry.status == ButtonStatus.NORMAL) {
			return true;
		} else if (entry.status == ButtonStatus.USELESS) {
			return ToolLevelingConfig.allowLevelingUselessEnchantments.getValue();
		} else if (entry.status == ButtonStatus.BREAK) {
			return ToolLevelingConfig.allowLevelingBreakingEnchantments.getValue();
		} else if (entry.status == ButtonStatus.BLACKLISTED || entry.status == ButtonStatus.CAPPED
				|| entry.status == ButtonStatus.MAXLEVEL) {
			return false;
		}
		return false;
	}

	public static ButtonEntry getButtonEntry(ToolLevelingTableScreen parent, Enchantment enchantment, int level) {
		List<Enchantment> whitelist = ToolLevelingConfig.enchantmentWhitelist.getValue();
		List<Enchantment> blacklist = ToolLevelingConfig.enchantmentBlacklist.getValue();
		ButtonEntry buttonEntry = new ButtonEntry(parent, enchantment, level);

		// leveling these enchantments will do absolutely nothing
		if (enchantment.getMaxLevel() == 1) {
			buttonEntry.status = ButtonStatus.USELESS;
		}
		// if whitelist is not empty, mark all enchantments as blacklisted if they are
		// not on the whitelist
		else if (!whitelist.isEmpty() && !whitelist.contains(enchantment)) {
			buttonEntry.status = ButtonStatus.BLACKLISTED;
		}
		// only list enchantments that are not on the blacklist
		else if (whitelist.isEmpty() && blacklist.contains(enchantment)) {
			buttonEntry.status = ButtonStatus.BLACKLISTED;
		}
		// although the level is defined as an integer, the actual maximum is a short
		// a higher enchantment level than a short will result in a negative level
		else if (level >= Short.MAX_VALUE) {
			buttonEntry.status = ButtonStatus.MAXLEVEL;
		}
		// check if the enchantment can still be leveled
		// some enchantments will break when leveled to high
		else if (Utils.willEnchantmentBreak(enchantment, level)) {
			buttonEntry.status = ButtonStatus.BREAK;
		}
		// check if the enchantment is allowed to level up
		// determinated by the config enchantmentCaps
		else if (Utils.isEnchantmentAtCap(enchantment, level)) {
			buttonEntry.status = ButtonStatus.CAPPED;
		}
		buttonEntry.updateButtonText();
		return buttonEntry;
	}

	public static Component getButtonText(ButtonEntry entry) {
		return new TranslatableComponent(entry.name).withStyle(getButtonTextFormatting(entry));
	}

	public static List<Component> getButtonToolTips(ButtonEntry data) {
		List<Component> tooltip = new ArrayList<>();
		tooltip.add(new TranslatableComponent(data.name).withStyle(ChatFormatting.AQUA));
		final String start = "container.toolleveling.tool_leveling_table";
		if (ButtonHelper.shouldButtonBeActive(data)) {
			tooltip.add(new TranslatableComponent(start + ".current_level", data.currentLevel)
					.withStyle(ChatFormatting.DARK_GRAY));
			tooltip.add(new TranslatableComponent(start + ".next_level", (data.currentLevel + 1))
					.withStyle(ChatFormatting.DARK_GRAY));
			tooltip.add(
					new TranslatableComponent(start + ".cost", data.upgradeCost).withStyle(ChatFormatting.DARK_GRAY));
		}
		if (data.status != ButtonStatus.NORMAL) {
			tooltip.add(new TranslatableComponent(start + ".error." + data.status.toString().toLowerCase())
					.withStyle(ButtonHelper.getButtonTextFormatting(data)));
		}
		return tooltip;
	}

	public static ChatFormatting getButtonTextFormatting(ButtonEntry entry) {
		ChatFormatting format = ChatFormatting.RESET;
		if (entry.status != ButtonStatus.NORMAL) {
			format = ChatFormatting.DARK_RED;
		}
		if (entry.status == ButtonStatus.USELESS) {
			format = ChatFormatting.YELLOW;
		}
		return format;
	}

	public static enum ButtonStatus {
		NORMAL, BLACKLISTED, USELESS, MAXLEVEL, BREAK, CAPPED;
	}
}
