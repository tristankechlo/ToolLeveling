package com.tristankechlo.toolleveling.commands;

import com.tristankechlo.toolleveling.config.util.ConfigIdentifier;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;

public final class ResponseHelper {

    public static void sendMessageConfigShow(CommandSourceStack source, ConfigIdentifier config) {
        MutableComponent clickableFile = clickableFile(config);
        MutableComponent text = new TextComponent(config.toString()).withStyle(ChatFormatting.GRAY);
        MutableComponent message = new TranslatableComponent("commands.toolleveling.config.path", text, clickableFile);
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), false);
    }

    public static void sendMessageConfigReload(CommandSourceStack source) {
        MutableComponent message = new TranslatableComponent("commands.toolleveling.config.reload");
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), true);
    }

    public static void sendMessageConfigReset(CommandSourceStack source, ConfigIdentifier config) {
        MutableComponent text = new TextComponent(config.toString()).withStyle(ChatFormatting.GRAY);
        MutableComponent message = new TranslatableComponent("commands.toolleveling.config.reset", text);
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), true);
    }

    public static void sendMessageConfigGeneral(CommandSourceStack source) {
        MutableComponent urlGeneral = clickableLink1(Names.URLS.CONFIG_START);
        MutableComponent general = new TranslatableComponent("commands.toolleveling.config.info_general", urlGeneral);
        sendMessage(source, general.withStyle(ChatFormatting.WHITE), false);
    }

    public static void sendMessageConfigSingle(CommandSourceStack source, ConfigIdentifier config) {
        MutableComponent url = clickableLink1(config.getInfoUrl());
        MutableComponent clickableFile = clickableFile(config);
        MutableComponent single = new TranslatableComponent("commands.toolleveling.config.info_single", clickableFile, url);
        sendMessage(source, single.withStyle(ChatFormatting.WHITE), false);
    }

    public static MutableComponent start() {
        return new TextComponent("[" + Names.MOD_NAME + "] ").withStyle(ChatFormatting.GOLD);
    }

    public static void sendMessage(CommandSourceStack source, Component message, boolean broadcastToOps) {
        MutableComponent start = start().append(message);
        source.sendSuccess(start, broadcastToOps);
    }

    public static MutableComponent clickableLink(String url, String displayText) {
        MutableComponent mutableComponent = new TextComponent(displayText);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static MutableComponent clickableLink2(String url) {
        return clickableLink(url, url);
    }

    public static MutableComponent clickableLink1(String url) {
        MutableComponent open = new TextComponent("[");
        MutableComponent click = new TranslatableComponent("commands.link.click_here");
        MutableComponent close = new TextComponent("]");
        MutableComponent result = open.append(click).append(close);
        result.withStyle(ChatFormatting.AQUA);
        return result.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static MutableComponent clickableFile(ConfigIdentifier config) {
        String fileName = config.getFileName();
        String filePath = ConfigManager.getConfigPath(config);
        MutableComponent mutableComponent = new TextComponent(fileName);
        mutableComponent.withStyle(ChatFormatting.GREEN, ChatFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        return mutableComponent;
    }

}
