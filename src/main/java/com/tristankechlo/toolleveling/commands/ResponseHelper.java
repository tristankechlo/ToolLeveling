package com.tristankechlo.toolleveling.commands;

import com.tristankechlo.toolleveling.config.util.ConfigIdentifier;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class ResponseHelper {

    public static void sendMessageConfigShow(ServerCommandSource source, ConfigIdentifier config) {
        MutableText clickableFile = clickableFile(config);
        MutableText text = Text.literal(config.toString()).formatted(Formatting.GRAY);
        MutableText message = Text.translatable("commands.toolleveling.config.path", text, clickableFile);
        sendMessage(source, message.formatted(Formatting.WHITE), false);
    }

    public static void sendMessageConfigReload(ServerCommandSource source) {
        MutableText message = Text.translatable("commands.toolleveling.config.reload");
        sendMessage(source, message.formatted(Formatting.WHITE), true);
    }

    public static void sendMessageConfigReset(ServerCommandSource source, ConfigIdentifier config) {
        MutableText text = Text.literal(config.toString()).formatted(Formatting.GRAY);
        MutableText message = Text.translatable("commands.toolleveling.config.reset", text);
        sendMessage(source, message.formatted(Formatting.WHITE), true);
    }

    public static void sendMessageConfigGeneral(ServerCommandSource source) {
        MutableText urlGeneral = clickableLink(Names.URLs.CONFIG_START);
        MutableText general = Text.translatable("commands.toolleveling.config.info_general", urlGeneral);
        sendMessage(source, general.formatted(Formatting.WHITE), false);
    }

    public static void sendMessageConfigSingle(ServerCommandSource source, ConfigIdentifier config) {
        MutableText url = clickableLink(config.getInfoUrl());
        MutableText clickableFile = clickableFile(config);
        MutableText single = Text.translatable("commands.toolleveling.config.info_single", clickableFile, url);
        sendMessage(source, single.formatted(Formatting.WHITE), false);
    }

    public static MutableText start() {
        return Text.literal("[" + Names.MOD_NAME + "] ").formatted(Formatting.GOLD);
    }

    public static void sendMessage(ServerCommandSource source, Text message, boolean broadcastToOps) {
        MutableText start = start().append(message);
        source.sendFeedback(start, broadcastToOps);
    }

    public static MutableText clickableLink(String url) {
        MutableText open = Text.literal("[");
        MutableText click = Text.translatable("commands.link.click_here");
        MutableText close = Text.literal("]");
        MutableText result = open.append(click).append(close);
        result.formatted(Formatting.AQUA);
        return result.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static MutableText clickableFile(ConfigIdentifier config) {
        String fileName = config.getFileName();
        String filePath = ConfigManager.getConfigPath(config);
        MutableText mutableText = Text.literal(fileName);
        mutableText.formatted(Formatting.GREEN, Formatting.UNDERLINE);
        mutableText.styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        return mutableText;
    }

}
