package com.tristankechlo.toolleveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.tristankechlo.toolleveling.config.CommandConfig;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EnchantmentArgument;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collection;
import java.util.Map;

public class SuperEnchantCommand {


    private static final DynamicCommandExceptionType NONLIVING_ENTITY_EXCEPTION;
    private static final DynamicCommandExceptionType INCOMPATIBLE_ENCHANTS_EXCEPTION;
    private static final DynamicCommandExceptionType WRONG_ENCHANTS_EXCEPTION;
    private static final DynamicCommandExceptionType ITEMLESS_EXCEPTION;
    private static final SimpleCommandExceptionType FAILED_EXCEPTION;

    static {
        NONLIVING_ENTITY_EXCEPTION = new DynamicCommandExceptionType((entityName) -> {
            return new TranslationTextComponent("commands.enchant.failed.entity", entityName);
        });
        INCOMPATIBLE_ENCHANTS_EXCEPTION = new DynamicCommandExceptionType((itemName) -> {
            return new TranslationTextComponent("commands.superenchant.failed.incompatible", itemName);
        });
        WRONG_ENCHANTS_EXCEPTION = new DynamicCommandExceptionType((itemName) -> {
            return new TranslationTextComponent("commands.superenchant.failed.wrong", itemName);
        });
        ITEMLESS_EXCEPTION = new DynamicCommandExceptionType((entityName) -> {
            return new TranslationTextComponent("commands.enchant.failed.itemless", entityName);
        });
        FAILED_EXCEPTION = new SimpleCommandExceptionType(new TranslationTextComponent("commands.enchant.failed"));
    }

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("superenchant").requires((player) -> {
            return player.hasPermission(3);
        }).then(Commands.argument("targets", EntityArgument.entities())
                .then(Commands.argument("enchantment", EnchantmentArgument.enchantment()).executes((context) -> {
                    return enchant(context.getSource(), EntityArgument.getEntities(context, "targets"),
                            EnchantmentArgument.getEnchantment(context, "enchantment"), 1);
                }).then(Commands.argument("level", IntegerArgumentType.integer(0, Short.MAX_VALUE))
                        .executes((context) -> {
                            return enchant(context.getSource(), EntityArgument.getEntities(context, "targets"),
                                    EnchantmentArgument.getEnchantment(context, "enchantment"),
                                    IntegerArgumentType.getInteger(context, "level"));
                        })))));
    }

    private static int enchant(CommandSource source, Collection<? extends Entity> targets, Enchantment enchantmentIn,
                               int level) throws CommandSyntaxException {
        int i = 0;

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingentity = (LivingEntity) entity;
                ItemStack stack = livingentity.getMainHandItem();
                if (!stack.isEmpty()) {
                    if (enchantmentIn.canEnchant(stack) || CommandConfig.allowWrongEnchantments.getValue()) {
                        if (EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantments(stack).keySet(), enchantmentIn)
                                || CommandConfig.allowIncompatibleEnchantments.getValue()) {

                            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
                            if (enchantments.containsKey(enchantmentIn) && level == 0) {
                                enchantments.remove(enchantmentIn);
                            } else {
                                enchantments.put(enchantmentIn, level);
                            }
                            EnchantmentHelper.setEnchantments(enchantments, stack);
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
                source.sendSuccess(new TranslationTextComponent("commands.enchant.success.single", enchantmentIn.getFullname(level), targets.iterator().next().getDisplayName()), true);
            } else {
                source.sendSuccess(new TranslationTextComponent("commands.enchant.success.multiple", enchantmentIn.getFullname(level), targets.size()), true);
            }

            return i;
        }
    }

}
