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

    public static void sendMessageConfigGeneral(ServerCommandSource source) {
        MutableText urlGeneral = Text.translatable("commands.link.click_here").formatted(Formatting.AQUA)
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Names.URLs.CONFIG_START)));
        MutableText general = Text.translatable("commands.toolleveling.config.info_general", urlGeneral).formatted(Formatting.WHITE);
        sendMessage(source, general, false);
    }

    public static void sendMessageConfigSingle(ServerCommandSource source, ConfigIdentifier config) {
        String filePath = ConfigManager.getConfigPath(config);
        MutableText url = Text.translatable("commands.link.click_here").formatted(Formatting.AQUA)
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, config.getInfoUrl())));
        MutableText fileName = Text.literal(config.getFileName()).formatted(Formatting.GREEN)
                .styled(style -> style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, filePath)));
        MutableText single = Text.translatable("commands.toolleveling.config.info_single", fileName, url).formatted(Formatting.WHITE);
        sendMessage(source, single, false);
    }

    public static void sendMessage(ServerCommandSource source, MutableText message, boolean broadcastToOps) {
        MutableText start = start().append(message);
        source.sendFeedback(start, broadcastToOps);
    }

    public static MutableText start() {
        return Text.literal("[" + Names.MOD_NAME + "] ").formatted(Formatting.GOLD);
    }

}
