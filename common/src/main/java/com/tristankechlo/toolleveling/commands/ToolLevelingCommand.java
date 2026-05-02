package com.tristankechlo.toolleveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.config.util.ConfigSyncing;
import com.tristankechlo.toolleveling.network.ClientBoundPacketHandler;
import com.tristankechlo.toolleveling.utils.ProjectLinks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;

public final class ToolLevelingCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands
                .literal(ToolLeveling.MOD_ID)
                .then(Commands.literal("config").requires((source) -> source.hasPermission(3))
                        .then(Commands.literal("reload").executes(ToolLevelingCommand::configReload))
                        .then(Commands.literal("reset").executes(ToolLevelingCommand::configReset)))
                .then(Commands.literal("openitemvalues").executes(ToolLevelingCommand::showScreen));

        ProjectLinks.registerAsCommand(command);
        dispatcher.register(command);
    }

    private static int configReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ConfigManager.loadAndVerifyConfigs();
        ConfigSyncing.syncAllConfigsToAllClients(context.getSource().getLevel());
        ResponseHelper.sendMessageConfigReload(source);
        return 1;
    }

    private static int configReset(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        // for loop over all configs and reset them
        ConfigManager.resetAllConfigs();
        ConfigSyncing.syncAllConfigsToAllClients(context.getSource().getLevel());
        ResponseHelper.sendMessageConfigReset(source);
        return 1;
    }

    private static int showScreen(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        ClientBoundPacketHandler.INSTANCE.openItemValueScreen(player);
        return 1;
    }

}
