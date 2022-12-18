package com.tristankechlo.toolleveling.commands;

import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.config.util.ConfigIdentifier;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;

public final class ResponseHelper {

    public static void sendMessageConfigShow(CommandSource source, ConfigIdentifier config) {
        ITextComponent clickableFile = clickableFile(config);
        IFormattableTextComponent text = new StringTextComponent(config.toString()).withStyle(TextFormatting.GRAY);
        IFormattableTextComponent message = new TranslationTextComponent("commands.toolleveling.config.path", text, clickableFile);
        sendMessage(source, message.withStyle(TextFormatting.WHITE), false);
    }

    public static void sendMessageConfigReload(CommandSource source) {
        IFormattableTextComponent message = new TranslationTextComponent("commands.toolleveling.config.reload");
        sendMessage(source, message.withStyle(TextFormatting.WHITE), true);
    }

    public static void sendMessageConfigReset(CommandSource source, ConfigIdentifier config) {
        IFormattableTextComponent text = new StringTextComponent(config.toString()).withStyle(TextFormatting.GRAY);
        IFormattableTextComponent message = new TranslationTextComponent("commands.toolleveling.config.reset", text);
        sendMessage(source, message.withStyle(TextFormatting.WHITE), true);
    }

    public static void sendMessageConfigGeneral(CommandSource source) {
        IFormattableTextComponent urlGeneral = clickableLink1(Names.URLS.CONFIG_START);
        IFormattableTextComponent general = new TranslationTextComponent("commands.toolleveling.config.info_general", urlGeneral);
        sendMessage(source, general.withStyle(TextFormatting.WHITE), false);
    }

    public static void sendMessageConfigSingle(CommandSource source, ConfigIdentifier config) {
        IFormattableTextComponent url = clickableLink1(config.getInfoUrl());
        IFormattableTextComponent clickableFile = clickableFile(config);
        IFormattableTextComponent single = new TranslationTextComponent("commands.toolleveling.config.info_single", clickableFile, url);
        sendMessage(source, single.withStyle(TextFormatting.WHITE), false);
    }

    public static IFormattableTextComponent start() {
        return new StringTextComponent("[" + Names.MOD_NAME + "] ").withStyle(TextFormatting.GOLD);
    }

    public static void sendMessage(CommandSource source, IFormattableTextComponent message, boolean broadcastToOps) {
        IFormattableTextComponent start = start().append(message);
        source.sendSuccess(start, broadcastToOps);
    }

    public static IFormattableTextComponent clickableLink(String url, String displayText) {
        IFormattableTextComponent mutableComponent = new StringTextComponent(displayText);
        mutableComponent.withStyle(TextFormatting.GREEN, TextFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
        return mutableComponent;
    }

    public static IFormattableTextComponent clickableLink2(String url) {
        return clickableLink(url, url);
    }

    public static IFormattableTextComponent clickableLink1(String url) {
        IFormattableTextComponent open = new StringTextComponent("[");
        IFormattableTextComponent click = new TranslationTextComponent("commands.link.click_here");
        IFormattableTextComponent close = new StringTextComponent("]");
        IFormattableTextComponent result = open.append(click).append(close);
        result.withStyle(TextFormatting.AQUA);
        return result.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, url)));
    }

    public static IFormattableTextComponent clickableFile(ConfigIdentifier config) {
        String fileName = config.getFileName();
        String filePath = ConfigManager.getConfigPath(config);
        IFormattableTextComponent mutableComponent = new StringTextComponent(fileName);
        mutableComponent.withStyle(TextFormatting.GREEN, TextFormatting.UNDERLINE);
        mutableComponent.withStyle(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        return mutableComponent;
    }

}

