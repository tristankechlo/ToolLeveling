package com.tristankechlo.toolleveling.commands;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.AbstractConfig;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ResponseHelper {

    public static void sendMessageConfigShow(CommandSourceStack source, AbstractConfig config) {
        MutableComponent clickableFile = clickableFile(config);
        MutableComponent text = Component.literal(config.getFileName()).withStyle(ChatFormatting.GRAY);
        MutableComponent message = Component.translatable("commands.toolleveling.config.path", text, clickableFile);
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), false);
    }

    public static void sendMessageConfigReload(CommandSourceStack source) {
        MutableComponent message = Component.translatable("commands.toolleveling.config.reload");
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), true);
    }

    public static void sendMessageConfigReset(CommandSourceStack source, AbstractConfig config) {
        MutableComponent text = Component.literal(config.getFileName()).withStyle(ChatFormatting.GRAY);
        MutableComponent message = Component.translatable("commands.toolleveling.config.reset", text);
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), true);
    }

    public static void sendMessageConfigGeneral(CommandSourceStack source) {
        MutableComponent urlGeneral = clickableLink1(ToolLeveling.CONFIG_START);
        MutableComponent general = Component.translatable("commands.toolleveling.config.info_general", urlGeneral);
        sendMessage(source, general.withStyle(ChatFormatting.WHITE), false);
    }

    public static void sendMessageConfigSingle(CommandSourceStack source, AbstractConfig config) {
        MutableComponent url = clickableLink1(config.getInfoUrl());
        MutableComponent clickableFile = clickableFile(config);
        MutableComponent single = Component.translatable("commands.toolleveling.config.info_single", clickableFile, url);
        sendMessage(source, single.withStyle(ChatFormatting.WHITE), false);
    }

    public static MutableComponent start() {
        return Component.literal("[" + ToolLeveling.MOD_NAME + "] ").withStyle(ChatFormatting.GOLD);
    }

    public static void sendMessage(CommandSourceStack source, Component message, boolean broadcastToOps) {
        MutableComponent start = start().append(message);
        source.sendSuccess(() -> start, broadcastToOps);
    }

    public static MutableComponent clickableLink(String url, String displayText) {
        MutableComponent mutableComponent = Component.literal(displayText);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static MutableComponent clickableLink2(String url) {
        return clickableLink(url, url);
    }

    public static MutableComponent clickableLink1(String url) {
        MutableComponent open = Component.literal("[");
        MutableComponent click = Component.translatable("commands.link.click_here");
        MutableComponent close = Component.literal("]");
        MutableComponent result = open.append(click).append(close);
        result.withStyle(ChatFormatting.AQUA);
        return result.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static MutableComponent clickableFile(AbstractConfig config) {
        String fileName = config.getFileName();
        String filePath = ConfigManager.getConfigPath(config);
        MutableComponent mutableComponent = Component.literal(fileName);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        return mutableComponent;
    }

}
