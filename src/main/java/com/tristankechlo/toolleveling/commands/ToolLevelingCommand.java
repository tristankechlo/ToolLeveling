package com.tristankechlo.toolleveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tristankechlo.toolleveling.config.ConfigManager;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.OpenItemValueScreenPacket;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.command.EnumArgument;

public final class ToolLevelingCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		LiteralArgumentBuilder<CommandSourceStack> toollevelingCommand = Commands
				.literal(
						"toolleveling")
				.then(Commands.literal("config").requires((source) -> source.hasPermission(3))
						.then(Commands.literal("reload").executes(context -> configReload(context)))
						.then(Commands.literal("show")
								.then(Commands.argument("identifier", EnumArgument.enumArgument(Identifier.class))
										.executes(context -> configShow(context))))
						.then(Commands.literal("reset")
								.then(Commands.argument("identifier", EnumArgument.enumArgument(Identifier.class))
										.executes(context -> configReset(context)))))
				.then(Commands.literal("openitemvalues").requires((source) -> source.hasPermission(0))
						.executes((c) -> showScreen(c)));
		dispatcher.register(toollevelingCommand);
	}

	private static int configReload(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		ConfigManager.reloadAllConfigs();
		source.sendSuccess(new TranslatableComponent("commands.toolleveling.config.reload"), true);
		return 1;
	}

	private static int configShow(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		final Identifier identifier = context.getArgument("identifier", Identifier.class);
		if (!ConfigManager.hasIdentifier(identifier.id)) {
			source.sendSuccess(new TranslatableComponent("commands.toolleveling.config.noconfig"), true);
			return -1;
		}
		String name = ConfigManager.getConfigFileName(identifier.id);
		String path = ConfigManager.getConfigPath(identifier.id);
		source.sendSuccess(
				new TranslatableComponent("commands.toolleveling.config.path",
						new TextComponent(name).withStyle(ChatFormatting.UNDERLINE).withStyle(
								(style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path)))),
				true);
		return 1;
	}

	private static int configReset(CommandContext<CommandSourceStack> context) {
		CommandSourceStack source = context.getSource();
		final Identifier identifier = context.getArgument("identifier", Identifier.class);
		if (!ConfigManager.hasIdentifier(identifier.id)) {
			source.sendSuccess(new TranslatableComponent("commands.toolleveling.config.noconfig"), true);
			return -1;
		}
		ConfigManager.resetOneConfig(identifier.id);
		source.sendSuccess(new TranslatableComponent("commands.toolleveling.config.reset", identifier.id), true);
		return 1;
	}

	private static int showScreen(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new OpenItemValueScreenPacket());
		return 1;
	}

	private static enum Identifier {
		GENERAL(Names.MOD_ID + ":general"),
		ITEMVALUES(Names.MOD_ID + ":itemValues");

		public final String id;

		private Identifier(String id) {
			this.id = id;
		}
	}

}
