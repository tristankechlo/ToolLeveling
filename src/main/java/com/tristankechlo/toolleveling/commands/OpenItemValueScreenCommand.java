package com.tristankechlo.toolleveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.OpenItemValueScreenPacket;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;

public final class OpenItemValueScreenCommand {

	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		dispatcher.register(Commands.literal("openitemvalues").requires((player) -> {
			return player.hasPermission(0);
		}).executes((context) -> showScreen(context)));
	}

	private static int showScreen(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
		ServerPlayer player = context.getSource().getPlayerOrException();
		PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new OpenItemValueScreenPacket());
		return 1;
	}

}
