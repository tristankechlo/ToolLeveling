package com.tristankechlo.toolleveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.OpenScreenPacket;

import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkDirection;

public final class ScreenCommand {

	public static void register(CommandDispatcher<CommandSource> dispatcher) {
		dispatcher.register(Commands.literal("screen").requires((player) -> {
			return player.hasPermissionLevel(3);
		}).executes((context) -> showScreen(context)));
	}

	private static int showScreen(CommandContext<CommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().asPlayer();
		PacketHandler.INSTANCE.sendTo(new OpenScreenPacket(), player.connection.getNetworkManager(),
				NetworkDirection.PLAY_TO_CLIENT);
		return 1;
	}

}
