package com.tristankechlo.toolleveling.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import java.util.Collection;
import java.util.Map;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;

import net.minecraft.command.argument.EnchantmentArgumentType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public final class SuperEnchantCommand {

	private static final DynamicCommandExceptionType NONLIVING_ENTITY_EXCEPTION;
	private static final DynamicCommandExceptionType INCOMPATIBLE_ENCHANTS_EXCEPTION;
	private static final DynamicCommandExceptionType WRONG_ENCHANTS_EXCEPTION;
	private static final DynamicCommandExceptionType ITEMLESS_EXCEPTION;
	private static final SimpleCommandExceptionType FAILED_EXCEPTION;

	static {
		NONLIVING_ENTITY_EXCEPTION = new DynamicCommandExceptionType((entityName) -> {
			return Text.translatable("commands.enchant.failed.entity", entityName);
		});
		INCOMPATIBLE_ENCHANTS_EXCEPTION = new DynamicCommandExceptionType((itemName) -> {
			return Text.translatable("commands.superenchant.failed.incompatible", itemName);
		});
		WRONG_ENCHANTS_EXCEPTION = new DynamicCommandExceptionType((itemName) -> {
			return Text.translatable("commands.superenchant.failed.wrong", itemName);
		});
		ITEMLESS_EXCEPTION = new DynamicCommandExceptionType((entityName) -> {
			return Text.translatable("commands.enchant.failed.itemless", entityName);
		});
		FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.enchant.failed"));
	}

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("superenchant").requires((player) -> {
			return player.hasPermissionLevel(3);
		}).then(argument("targets", EntityArgumentType.entities())
				.then(argument("enchantment", EnchantmentArgumentType.enchantment()).executes((context) -> {
					return enchant(context.getSource(), EntityArgumentType.getEntities(context, "targets"),
							EnchantmentArgumentType.getEnchantment(context, "enchantment"), 1);
				}).then(argument("level", IntegerArgumentType.integer(0, Short.MAX_VALUE)).executes((context) -> {
					return enchant(context.getSource(), EntityArgumentType.getEntities(context, "targets"),
							EnchantmentArgumentType.getEnchantment(context, "enchantment"),
							IntegerArgumentType.getInteger(context, "level"));
				})))));
	}

	private static int enchant(ServerCommandSource source, Collection<? extends Entity> targets,
			Enchantment enchantmentIn, int level) throws CommandSyntaxException {
		int i = 0;

		for (Entity entity : targets) {
			if (entity instanceof LivingEntity) {
				LivingEntity livingentity = (LivingEntity) entity;
				ItemStack stack = livingentity.getMainHandStack();
				if (!stack.isEmpty()) {
					if (enchantmentIn.isAcceptableItem(stack) || ToolLevelingConfig.allowWrongEnchantments.getValue()) {
						if (EnchantmentHelper.isCompatible(EnchantmentHelper.get(stack).keySet(), enchantmentIn)
								|| ToolLevelingConfig.allowIncompatibleEnchantments.getValue()) {

							Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(stack);
							if (enchantments.containsKey(enchantmentIn) && level == 0) {
								enchantments.remove(enchantmentIn);
							} else {
								enchantments.put(enchantmentIn, level);
							}
							EnchantmentHelper.set(enchantments, stack);
							i++;

						} else if (targets.size() == 1) {
							throw INCOMPATIBLE_ENCHANTS_EXCEPTION.create(stack.getItem().getName(stack).getString());
						}
					} else if (targets.size() == 1) {
						throw WRONG_ENCHANTS_EXCEPTION.create(stack.getItem().getName(stack).getString());
					}
				} else if (targets.size() == 1) {
					throw ITEMLESS_EXCEPTION.create(livingentity.getName().getString());
				}
			} else if (targets.size() == 1) {
				throw NONLIVING_ENTITY_EXCEPTION.create(entity.getName().getString());
			}
		}

		if (i == 0) {
			throw FAILED_EXCEPTION.create();
		} else {
			if (targets.size() == 1) {
				source.sendFeedback(Text.translatable("commands.enchant.success.single", enchantmentIn.getName(level),
						targets.iterator().next().getDisplayName()), true);
			} else {
				source.sendFeedback(Text.translatable("commands.enchant.success.multiple", enchantmentIn.getName(level),
						targets.size()), true);
			}

			return i;
		}
	}

}
