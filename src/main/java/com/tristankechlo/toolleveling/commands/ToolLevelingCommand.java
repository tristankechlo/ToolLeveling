package com.tristankechlo.toolleveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tristankechlo.toolleveling.config.util.ConfigIdentifier;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.OpenItemValueScreenPacket;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.command.EnumArgument;

public final class ToolLevelingCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> command = Commands
                .literal(Names.MOD_ID)
                .then(Commands.literal("config").requires((source) -> source.hasPermission(3))
                        .then(Commands.literal("reload").executes(ToolLevelingCommand::configReload))
                        .then(Commands.literal("show").then(Commands.argument("identifier", EnumArgument.enumArgument(ConfigIdentifier.class))
                                .executes(ToolLevelingCommand::configShow)))
                        .then(Commands.literal("reset").then(Commands.argument("identifier", EnumArgument.enumArgument(ConfigIdentifier.class))
                                .executes(ToolLevelingCommand::configReset)))
                        .then(Commands.literal("info").then(Commands.argument("identifier", EnumArgument.enumArgument(ConfigIdentifier.class))
                                .executes(ToolLevelingCommand::configInfo))))
                .then(Commands.literal("openitemvalues").requires((source) -> source.hasPermission(0))
                        .executes(ToolLevelingCommand::showScreen))
                .then(Commands.literal("github").executes(ToolLevelingCommand::github))
                .then(Commands.literal("issue").executes(ToolLevelingCommand::issue))
                .then(Commands.literal("wiki").executes(ToolLevelingCommand::wiki))
                .then(Commands.literal("discord").executes(ToolLevelingCommand::discord))
                .then(Commands.literal("curseforge").executes(ToolLevelingCommand::curseforge))
                .then(Commands.literal("modrinth").executes(ToolLevelingCommand::modrinth));
        dispatcher.register(command);
    }

    private static int configReload(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        ConfigManager.reloadAllConfigs();
        ResponseHelper.sendMessageConfigReload(source);
        return 1;
    }

    private static int configShow(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);
        ResponseHelper.sendMessageConfigShow(source, config);
        return 1;
    }

    private static int configReset(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);
        ConfigManager.resetOneConfig(config);
        ResponseHelper.sendMessageConfigReset(source, config);
        return 1;
    }

    private static int configInfo(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);

        ResponseHelper.sendMessageConfigGeneral(source);
        ResponseHelper.sendMessageConfigSingle(source, config);
        return 1;
    }

    private static int showScreen(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new OpenItemValueScreenPacket());
        return 1;
    }

    private static int github(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink2(Names.URLS.GITHUB);
        Component message = new TextComponent("Check out the source code on GitHub: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int issue(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink2(Names.URLS.GITHUB_ISSUE);
        Component message = new TextComponent("If you found an issue, submit it here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int wiki(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink2(Names.URLS.GITHUB_WIKI);
        Component message = new TextComponent("The wiki can be found here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int discord(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink2(Names.URLS.DISCORD);
        Component message = new TextComponent("Join the Discord here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int curseforge(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink2(Names.URLS.CURSEFORGE);
        Component message = new TextComponent("Check out the CurseForge page here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int modrinth(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        Component link = ResponseHelper.clickableLink2(Names.URLS.MODRINTH);
        Component message = new TextComponent("Check out the Modrinth page here: ").withStyle(ChatFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

}
