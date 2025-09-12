package com.tristankechlo.toolleveling.utils;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.tristankechlo.toolleveling.commands.ResponseHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public enum ProjectLinks {

    GITHUB("Check out the source code on GitHub: ", "https://github.com/tristankechlo/LivingThings"),
    ISSUE("If you found an issue, submit it here: ", "https://github.com/tristankechlo/LivingThings/issues"),
    WIKI("The wiki can be found here: ", "https://github.com/tristankechlo/LivingThings/wiki"),
    DISCORD("Join the Discord here: ", "https://discord.gg/bhUaWhq"),
    CURSEFORGE("Check out the CurseForge page here: ", "https://curseforge.com/minecraft/mc-mods/living-things"),
    MODRINTH("Check out the Modrinth page here: ", "https://modrinth.com/mod/living-things");

    private final MutableComponent message;
    public final String url;

    ProjectLinks(String message, String url) {
        this.message = new TextComponent(message);
        this.message.withStyle(ChatFormatting.WHITE);
        this.message.append(ResponseHelper.clickableLink(url, url));
        this.url = url;
    }

    public int execute(CommandContext<CommandSourceStack> sender) {
        ResponseHelper.sendMessage(sender.getSource(), message, false);
        return 1;
    }

    public static void registerAsCommand(LiteralArgumentBuilder<CommandSourceStack> command) {
        for (ProjectLinks link : values()) {
            command.then(Commands.literal(link.name().toLowerCase()).executes(link::execute));
        }
    }

}