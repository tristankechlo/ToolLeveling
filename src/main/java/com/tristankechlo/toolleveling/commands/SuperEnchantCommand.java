package com.tristankechlo.toolleveling.commands;

import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.tristankechlo.toolleveling.ToolLeveling;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SuperEnchantCommand {
	
	private static ITextComponent LEVEL_WRONG = new TranslationTextComponent("commands."+ToolLeveling.MOD_ID+".superenchant.failed.level");
	private static ITextComponent NOT_ENCHANTET = new TranslationTextComponent("commands."+ToolLeveling.MOD_ID+".superenchant.failed.not");

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("superenchant").requires((player) -> {
			return player.hasPermissionLevel(3);
		}).then(Commands.argument("level", IntegerArgumentType.integer(0, 32767)).executes((source) -> {
			return enchant(source.getSource(), IntegerArgumentType.getInteger(source, "level"));
		})));
	}
	
	private static int enchant(CommandSource source, int level) {
		if(source.getEntity() instanceof ServerPlayerEntity) {
			if(level >= 1 && level <= Short.MAX_VALUE) {
				ServerPlayerEntity player = (ServerPlayerEntity) source.getEntity();
				ItemStack stack = player.getHeldItemMainhand();
				if(stack.isEnchanted()) {
					Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
					for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
						//only update enchantments which can be higher than 1, to ignore enchantments like silk-touch
						if(entry.getKey().getMaxLevel() > 1) {
							enchantments.put(entry.getKey(), level);
						}
				    }
					EnchantmentHelper.setEnchantments(enchantments, stack);
				} else {
					source.sendFeedback(NOT_ENCHANTET, true);
				}
			} else {
				source.sendFeedback(LEVEL_WRONG, true);
			}
		}
		return 0;
	}

}
