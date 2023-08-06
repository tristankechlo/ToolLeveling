package com.tristankechlo.toolleveling;

import com.mojang.brigadier.CommandDispatcher;
import com.tristankechlo.toolleveling.commands.SuperEnchantCommand;
import com.tristankechlo.toolleveling.commands.ToolLevelingCommand;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.config.util.ConfigSyncingHelper;
import com.tristankechlo.toolleveling.network.FabricServerNetworkHelper;
import com.tristankechlo.toolleveling.network.ServerNetworkHelper;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.CreativeModeTabs;

public class FabricToolLeveling implements ModInitializer {

    @Override
    public void onInitialize() {
        ToolLeveling.init(); // register all items, blocks, etc.
        ServerNetworkHelper.setup(); // register server related network stuff
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(this::populateCreativeTab);
        CommandRegistrationCallback.EVENT.register(this::registerCommands);

        // server start event
        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            // make sure config folder exists
            ConfigManager.createConfigFolder();
            // load configs from file
            ConfigManager.setup();
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            // send server-config to player
            ConfigSyncingHelper.syncAllConfigsToOneClient(handler.getPlayer());
        });
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        ToolLevelingCommand.register(dispatcher);
        SuperEnchantCommand.register(dispatcher, context);
    }

    private void populateCreativeTab(FabricItemGroupEntries entries) {
        entries.accept(ToolLeveling.TLT_ITEM.get());
    }

}