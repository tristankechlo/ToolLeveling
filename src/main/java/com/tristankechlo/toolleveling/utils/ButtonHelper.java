package com.tristankechlo.toolleveling.utils;

import java.util.ArrayList;
import java.util.List;

import com.tristankechlo.toolleveling.client.screen.ButtonEntry;
import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class ButtonHelper {

	public static boolean shouldButtonBeActive(ButtonEntry entry) {
		if (entry.status == ButtonStatus.NORMAL) {
			return true;
		} else if (entry.status == ButtonStatus.USELESS) {
			return ToolLevelingConfig.allowLevelingUselessEnchantments;
		} else if (entry.status == ButtonStatus.BREAK) {
			return ToolLevelingConfig.allowLevelingBreakingEnchantments;
		} else if (entry.status == ButtonStatus.BLACKLISTED || entry.status == ButtonStatus.CAPPED
				|| entry.status == ButtonStatus.MAXLEVEL) {
			return false;
		}
		return false;
	}

	public static ButtonEntry getButtonEntry(ToolLevelingTableScreen parent, Enchantment enchantment, int level) {
		List<Enchantment> blacklist = ToolLevelingConfig.enchantmentBlacklist;
		ButtonEntry buttonEntry = new ButtonEntry(parent, enchantment, level);

		// leveling these enchantments will do absolutely nothing
		if (enchantment.getMaxLevel() == 1) {
			buttonEntry.status = ButtonStatus.USELESS;
		}
		// only list enchantments that are not on the blacklist
		else if (blacklist.contains(enchantment)) {
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

	public static ITextComponent getButtonText(ButtonEntry entry) {
		return new TranslationTextComponent(entry.name).mergeStyle(getButtonTextFormatting(entry));
	}

	public static List<ITextComponent> getButtonToolTips(ButtonEntry data) {
		List<ITextComponent> tooltip = new ArrayList<>();
		tooltip.add(new TranslationTextComponent(data.name).mergeStyle(TextFormatting.AQUA));
		final String start = "container.toolleveling.tool_leveling_table";
		if (ButtonHelper.shouldButtonBeActive(data)) {
			tooltip.add(new TranslationTextComponent(start + ".current_level", data.currentLevel)
					.mergeStyle(TextFormatting.DARK_GRAY));
			tooltip.add(new TranslationTextComponent(start + ".next_level", (data.currentLevel + 1))
					.mergeStyle(TextFormatting.DARK_GRAY));
			tooltip.add(new TranslationTextComponent(start + ".cost", data.upgradeCost)
					.mergeStyle(TextFormatting.DARK_GRAY));
		}
		if (data.status != ButtonStatus.NORMAL) {
			tooltip.add(new TranslationTextComponent(start + ".error." + data.status.toString().toLowerCase())
					.mergeStyle(ButtonHelper.getButtonTextFormatting(data)));
		}
		return tooltip;
	}

	public static TextFormatting getButtonTextFormatting(ButtonEntry entry) {
		TextFormatting format = TextFormatting.RESET;
		if (entry.status != ButtonStatus.NORMAL) {
			format = TextFormatting.DARK_RED;
		}
		if (entry.status == ButtonStatus.USELESS) {
			format = TextFormatting.YELLOW;
		}
		return format;
	}

	public static enum ButtonStatus {
		NORMAL, BLACKLISTED, USELESS, MAXLEVEL, BREAK, CAPPED;
	}
}
