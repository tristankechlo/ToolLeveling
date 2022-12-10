package com.tristankechlo.toolleveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.ConfigIdentifier;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.network.ServerNetworkHandler;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public final class ToolLevelingCommand {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("toolleveling")
                .then(literal("config").requires((source) -> source.hasPermissionLevel(3))
                        .then(literal("reload").executes(ToolLevelingCommand::configReload))
                        .then(literal("show").then(argument("identifier", ConfigIdentifierArgumentType.get())
                                .executes(ToolLevelingCommand::configShow)))
                        .then(literal("reset").then(argument("identifier", ConfigIdentifierArgumentType.get())
                                .executes(ToolLevelingCommand::configReset)))
                        .then(literal("info").then(argument("identifier", ConfigIdentifierArgumentType.get())
                                .executes(ToolLevelingCommand::configInfo))))
                .then(literal("openitemvalues").requires((source) -> source.hasPermissionLevel(0)).executes(ToolLevelingCommand::showScreen))
                .then(literal("github").executes(ToolLevelingCommand::github))
                .then(literal("issue").executes(ToolLevelingCommand::issue))
                .then(literal("wiki").executes(ToolLevelingCommand::wiki))
                .then(literal("discord").executes(ToolLevelingCommand::discord))
                .then(literal("curseforge").executes(ToolLevelingCommand::curseforge))
                .then(literal("modrinth").executes(ToolLevelingCommand::modrinth));
        dispatcher.register(command);
    }

    private static int configReload(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ConfigManager.reloadAllConfigs(source.getServer());
        ResponseHelper.sendMessageConfigReload(source);
        return 1;
    }

    private static int configShow(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);
        ResponseHelper.sendMessageConfigShow(source, config);
        return 1;
    }

    private static int configReset(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);
        ConfigManager.resetOneConfig(source.getServer(), config);
        ResponseHelper.sendMessageConfigReset(source, config);
        return 1;
    }

    private static int configInfo(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);

        ResponseHelper.sendMessageConfigGeneral(source);
        ResponseHelper.sendMessageConfigSingle(source, config);
        return 1;
    }

    private static int showScreen(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            ServerNetworkHandler.sendOpenItemValues(player);
        } catch (Exception e) {
            ToolLeveling.LOGGER.error("Error while executing command '/toolleveling openitemvalues'!\n" + e.getMessage());
            return 0;
        }
        return 1;
    }

    private static int github(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink2(Names.URLS.GITHUB);
        MutableText message = Text.literal("Check out the source code on GitHub: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int issue(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink2(Names.URLS.GITHUB_ISSUE);
        MutableText message = Text.literal("If you found an issue, submit it here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int wiki(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink2(Names.URLS.GITHUB_WIKI);
        MutableText message = Text.literal("The wiki can be found here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int discord(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink2(Names.URLS.DISCORD);
        MutableText message = Text.literal("Join the Discord here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int curseforge(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink2(Names.URLS.CURSEFORGE);
        MutableText message = Text.literal("Check out the CurseForge page here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int modrinth(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        MutableText link = ResponseHelper.clickableLink2(Names.URLS.MODRINTH);
        MutableText message = Text.literal("Check out the Modrinth page here: ").formatted(Formatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

}
