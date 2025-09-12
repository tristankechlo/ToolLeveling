package com.tristankechlo.toolleveling;

import com.tristankechlo.toolleveling.commands.SuperEnchantCommand;
import com.tristankechlo.toolleveling.commands.ToolLevelingCommand;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.network.FabricPacketHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class FabricToolLeveling implements ModInitializer {

    @Override
    public void onInitialize() {
        // register network packets
        FabricPacketHandler.registerPackets();

        // register items, blocks, etc.
        ModRegistry.load();

        // register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            ToolLevelingCommand.register(dispatcher);
            SuperEnchantCommand.register(dispatcher);
        });

        // server start evet
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            ConfigManager.createConfigFolder();
            ConfigManager.loadAndVerifyConfigs();
        });

    }

}
