package com.tristankechlo.toolleveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.config.util.ConfigIdentifier;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.network.packets.OpenItemValueScreenPacket;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.server.command.EnumArgument;

public class ToolLevelingCommand {

    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralArgumentBuilder<CommandSource> toollevelingCommand = Commands
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
        dispatcher.register(toollevelingCommand);
    }

    private static int configReload(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        ConfigManager.reloadAllConfigs();
        ResponseHelper.sendMessageConfigReload(source);
        return 1;
    }

    private static int configShow(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);
        ResponseHelper.sendMessageConfigShow(source, config);
        return 1;
    }

    private static int configReset(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);
        ConfigManager.resetOneConfig(config);
        ResponseHelper.sendMessageConfigReset(source, config);
        return 1;
    }

    private static int configInfo(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);

        ResponseHelper.sendMessageConfigGeneral(source);
        ResponseHelper.sendMessageConfigSingle(source, config);
        return 1;
    }

    private static int showScreen(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().getPlayerOrException();
        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new OpenItemValueScreenPacket());
        return 1;
    }

    private static int github(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink2(Names.URLS.GITHUB);
        IFormattableTextComponent message = new StringTextComponent("Check out the source code on GitHub: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int issue(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink2(Names.URLS.GITHUB_ISSUE);
        IFormattableTextComponent message = new StringTextComponent("If you found an issue, submit it here: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int wiki(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink2(Names.URLS.GITHUB_WIKI);
        IFormattableTextComponent message = new StringTextComponent("The wiki can be found here: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int discord(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink2(Names.URLS.DISCORD);
        IFormattableTextComponent message = new StringTextComponent("Join the Discord here: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int curseforge(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink2(Names.URLS.CURSEFORGE);
        IFormattableTextComponent message = new StringTextComponent("Check out the CurseForge page here: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

    private static int modrinth(CommandContext<CommandSource> context) {
        CommandSource source = context.getSource();
        IFormattableTextComponent link = ResponseHelper.clickableLink2(Names.URLS.MODRINTH);
        IFormattableTextComponent message = new StringTextComponent("Check out the Modrinth page here: ").withStyle(TextFormatting.WHITE).append(link);
        ResponseHelper.sendMessage(source, message, false);
        return 1;
    }

}
