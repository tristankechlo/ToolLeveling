package com.tristankechlo.toolleveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.toolleveling.config.ConfigManager;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.server.command.EnumArgument;

public class ToolLevelingCommand {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		LiteralArgumentBuilder<CommandSource> toollevelingCommand = Commands.literal("toolleveling")
				.requires(
						(source) -> source.hasPermission(3))
				.then(Commands.literal("config")
						.then(Commands.literal("reload").executes(context -> configReload(context)))
						.then(Commands.literal("show")
								.then(Commands.argument("identifier", EnumArgument.enumArgument(Identifier.class))
										.executes(context -> configShow(context))))
						.then(Commands.literal("reset")
								.then(Commands.argument("identifier", EnumArgument.enumArgument(Identifier.class))
										.executes(context -> configReset(context)))));
		dispatcher.register(toollevelingCommand);
	}

	private static int configReload(CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();
		ConfigManager.reloadAllConfigs();
		source.sendSuccess(new TranslationTextComponent("commands.toolleveling.config.reload"), true);
		return 1;
	}

	private static int configShow(CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();
		final Identifier identifier = context.getArgument("identifier", Identifier.class);
		if (!ConfigManager.hasIdentifier(identifier.id)) {
			source.sendSuccess(new TranslationTextComponent("commands.toolleveling.config.noconfig"), true);
			return -1;
		}
		String name = ConfigManager.getConfigFileName(identifier.id);
		String path = ConfigManager.getConfigPath(identifier.id);
		source.sendSuccess(
				new TranslationTextComponent("commands.toolleveling.config.path",
						new StringTextComponent(name).withStyle(TextFormatting.UNDERLINE).withStyle(
								(style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path)))),
				true);
		return 1;
	}

	private static int configReset(CommandContext<CommandSource> context) {
		CommandSource source = context.getSource();
		final Identifier identifier = context.getArgument("identifier", Identifier.class);
		if (!ConfigManager.hasIdentifier(identifier.id)) {
			source.sendSuccess(new TranslationTextComponent("commands.toolleveling.config.noconfig"), true);
			return -1;
		}
		ConfigManager.resetOneConfig(identifier.id);
		source.sendSuccess(new TranslationTextComponent("commands.toolleveling.config.reset", identifier.id), true);
		return 1;
	}

	private static enum Identifier {
		GENERAL(Names.MOD_ID + ":general"), ITEMVALUES(Names.MOD_ID + ":itemValues");

		public final String id;

		private Identifier(String id) {
			this.id = id;
		}
	}
}
