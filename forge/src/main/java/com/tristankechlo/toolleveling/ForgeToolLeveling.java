package com.tristankechlo.toolleveling;

import com.mojang.brigadier.CommandDispatcher;
import com.tristankechlo.toolleveling.commands.SuperEnchantCommand;
import com.tristankechlo.toolleveling.commands.ToolLevelingCommand;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.network.ForgePacketHandler;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(Names.MOD_ID)
public final class ForgeToolLeveling {

    public ForgeToolLeveling() {
        ForgePacketHandler.registerPackets();
        ModRegistry.load();

        MinecraftForge.EVENT_BUS.addListener(this::serverStartup);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
    }

    private void serverStartup(final ServerStartingEvent event) {
        ConfigManager.createConfigFolder();
        ConfigManager.loadAndVerifyConfigs();
    }

    public void registerCommands(final RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        SuperEnchantCommand.register(dispatcher);
        ToolLevelingCommand.register(dispatcher);
    }

}
