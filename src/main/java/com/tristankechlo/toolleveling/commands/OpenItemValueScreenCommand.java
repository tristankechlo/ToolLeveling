package com.tristankechlo.toolleveling.commands;

import static net.minecraft.server.command.CommandManager.literal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.ServerNetworkHandler;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public final class OpenItemValueScreenCommand {

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
		dispatcher.register(literal("openitemvalues").requires((player) -> {
			return player.hasPermissionLevel(0);
		}).executes(OpenItemValueScreenCommand::showScreen));
	}

	private static int showScreen(CommandContext<ServerCommandSource> context) {
		try {
			ServerPlayerEntity player = context.getSource().getPlayer();
			ServerNetworkHandler.sendOpenItemValues(player);
		} catch (CommandSyntaxException e) {
			ToolLeveling.LOGGER.error("Error while sending command '/openitemvalues', invalid sender!");
			return 0;
		} catch (Exception e) {
			ToolLeveling.LOGGER.error("Error while sending command '/openitemvalues'!\n" + e.getMessage());
			return 0;
		}
		return 1;
	}

}
