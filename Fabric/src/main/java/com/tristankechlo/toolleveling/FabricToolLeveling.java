package com.tristankechlo.toolleveling;

import com.mojang.brigadier.CommandDispatcher;
import com.tristankechlo.toolleveling.commands.SuperEnchantCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroupEntries;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.item.CreativeModeTabs;

public class FabricToolLeveling implements ModInitializer {

    @Override
    public void onInitialize() {
        ToolLeveling.init();
        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(this::populateCreativeTab);
        CommandRegistrationCallback.EVENT.register(this::registerCommands);
    }

    private void registerCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection selection) {
        SuperEnchantCommand.register(dispatcher, context);
    }

    private void populateCreativeTab(FabricItemGroupEntries entries) {
        entries.accept(ToolLeveling.TLT_ITEM.get());
    }

}