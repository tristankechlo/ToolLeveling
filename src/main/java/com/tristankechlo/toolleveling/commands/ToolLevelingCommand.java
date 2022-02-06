package com.tristankechlo.toolleveling.commands;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.toolleveling.config.ConfigManager;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Formatting;

public class ToolLevelingCommand {

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> toollevelingCommand = literal("toolleveling")
				.requires((source) -> source.hasPermissionLevel(3))
				.then(literal("config").then(literal("reload").executes(ToolLevelingCommand::configReload))
						.then(literal("show")
								.then(argument("identifier", EnumArgument.enumArgument(ConfigIdentifier.class))
										.executes(ToolLevelingCommand::configShow)))
						.then(literal("reset")
								.then(argument("identifier", EnumArgument.enumArgument(ConfigIdentifier.class))
										.executes(ToolLevelingCommand::configReset))));
		dispatcher.register(toollevelingCommand);
	}

	private static int configReload(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		ConfigManager.reloadAllConfigs(source.getServer());
		source.sendFeedback(new TranslatableText("commands.toolleveling.config.reload"), true);
		return 1;
	}

	private static int configShow(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		final ConfigIdentifier identifier = context.getArgument("identifier", ConfigIdentifier.class);
		if (!ConfigManager.hasIdentifier(identifier.id)) {
			source.sendFeedback(new TranslatableText("commands.toolleveling.config.noconfig"), true);
			return -1;
		}
		String name = ConfigManager.getConfigFileName(identifier.id);
		String path = ConfigManager.getConfigPath(identifier.id);
		MutableText mutableText = (new LiteralText(name)).formatted(Formatting.UNDERLINE)
				.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path)));
		source.sendFeedback(new TranslatableText("commands.toolleveling.config.path", mutableText), true);
		return 1;
	}

	private static int configReset(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		final ConfigIdentifier identifier = context.getArgument("identifier", ConfigIdentifier.class);
		if (!ConfigManager.hasIdentifier(identifier.id)) {
			source.sendFeedback(new TranslatableText("commands.toolleveling.config.noconfig"), true);
			return -1;
		}
		ConfigManager.resetOneConfig(source.getServer(), identifier.id);
		source.sendFeedback(new TranslatableText("commands.toolleveling.config.reset", identifier.id), true);
		return 1;
	}

	public static enum ConfigIdentifier {
		GENERAL(Names.MOD_ID + ":general"),
		ITEMVALUES(Names.MOD_ID + ":itemValues");

		public final String id;

		private ConfigIdentifier(String id) {
			this.id = id;
		}
	}
}
