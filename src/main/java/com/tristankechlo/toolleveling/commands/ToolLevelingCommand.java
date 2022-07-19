package com.tristankechlo.toolleveling.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.ConfigIdentifier;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.network.ServerNetworkHandler;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

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
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);
        String name = config.getFileName();
        String path = ConfigManager.getConfigPath(config);
        MutableText mutableText = (Text.literal(name)).formatted(Formatting.UNDERLINE).styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, path)));
        source.sendFeedback(Text.translatable("commands.toolleveling.config.path", mutableText), true);
        return 1;
    }

    private static int configReset(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        final ConfigIdentifier config = context.getArgument("identifier", ConfigIdentifier.class);
        ConfigManager.resetOneConfig(source.getServer(), config);
        source.sendFeedback(Text.translatable("commands.toolleveling.config.reset", config.withModID()), true);
        ToolLeveling.LOGGER.error(ConfigIdentifier.values());
        return 1;
    }

    private static int showScreen(CommandContext<ServerCommandSource> context) {
        try {
            ServerPlayerEntity player = context.getSource().getPlayer();
            ServerNetworkHandler.sendOpenItemValues(player);
        } catch (Exception e) {
            ToolLeveling.LOGGER.error("Error while sending command '/toolleveling openitemvalues'!\n" + e.getMessage());
            return 0;
        }
        return 1;
    }
}
