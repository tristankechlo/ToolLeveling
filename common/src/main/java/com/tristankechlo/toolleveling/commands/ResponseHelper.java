package com.tristankechlo.toolleveling.commands;

import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;

public final class ResponseHelper {

    public static void sendMessageConfigReload(CommandSourceStack source) {
        MutableComponent message = new TranslatableComponent("commands.toolleveling.config.reload");
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), true);
    }

    public static void sendMessageConfigReset(CommandSourceStack source) {
        MutableComponent message = new TranslatableComponent("commands.toolleveling.config.reset");
        sendMessage(source, message.withStyle(ChatFormatting.WHITE), true);
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

}
