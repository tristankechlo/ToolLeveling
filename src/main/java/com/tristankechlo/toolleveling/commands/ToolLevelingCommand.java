package com.tristankechlo.toolleveling.commands;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.ConfigManager;
import com.tristankechlo.toolleveling.network.ServerNetworkHandler;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ToolLevelingCommand {

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		LiteralArgumentBuilder<ServerCommandSource> toollevelingCommand = literal("toolleveling")
				.then(literal("config").requires((source) -> source.hasPermissionLevel(3))
						.then(literal("reload").executes(ToolLevelingCommand::configReload))
						.then(literal("show").then(argument("identifier", ConfigIdentifierArgumentType.get())
								.executes(ToolLevelingCommand::configShow)))
						.then(literal("reset").then(argument("identifier", ConfigIdentifierArgumentType.get())
								.executes(ToolLevelingCommand::configReset))))
				.then(literal("openitemvalues").requires((source) -> source.hasPermissionLevel(0)).executes(ToolLevelingCommand::showScreen));
		dispatcher.register(toollevelingCommand);
	}

	private static int configReload(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		ConfigManager.reloadAllConfigs(source.getServer());
		source.sendFeedback(Text.translatable("commands.toolleveling.config.reload"), true);
		return 1;
	}

	private static int configShow(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		final ConfigIdentifier identifier = context.getArgument("identifier", ConfigIdentifier.class);
		if (!ConfigManager.hasIdentifier(identifier.withModID())) {
			source.sendFeedback(Text.translatable("commands.toolleveling.config.noconfig"), true);
			return -1;
		}
		String name = ConfigManager.getConfigFileName(identifier.withModID());
		String path = ConfigManager.getConfigPath(identifier.withModID());
		MutableText mutableText = (Text.literal(name)).formatted(Formatting.UNDERLINE)
				.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path)));
		source.sendFeedback(Text.translatable("commands.toolleveling.config.path", mutableText), true);
		return 1;
	}

	private static int configReset(CommandContext<ServerCommandSource> context) {
		ServerCommandSource source = context.getSource();
		final ConfigIdentifier identifier = context.getArgument("identifier", ConfigIdentifier.class);
		if (!ConfigManager.hasIdentifier(identifier.withModID())) {
			source.sendFeedback(Text.translatable("commands.toolleveling.config.noconfig"), true);
			return -1;
		}
		ConfigManager.resetOneConfig(source.getServer(), identifier.withModID());
		source.sendFeedback(Text.translatable("commands.toolleveling.config.reset", identifier.withModID()), true);
		ToolLeveling.LOGGER.error(ConfigIdentifier.values());
		return 1;
	}

	private static int showScreen(CommandContext<ServerCommandSource> context) {
		try {
			ServerPlayerEntity player = context.getSource().getPlayer();
			ServerNetworkHandler.sendOpenItemValues(player);
		} catch (Exception e) {
			ToolLeveling.LOGGER.error("Error while sending command '/openitemvalues'!\n" + e.getMessage());
			return 0;
		}
		return 1;
	}
}
