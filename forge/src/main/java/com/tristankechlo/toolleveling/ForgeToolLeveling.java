package com.tristankechlo.toolleveling;

import com.mojang.brigadier.CommandDispatcher;
import com.tristankechlo.toolleveling.commands.SuperEnchantCommand;
import com.tristankechlo.toolleveling.commands.ToolLevelingCommand;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.network.ForgePacketHandler;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ToolLeveling.MOD_ID)
public final class ForgeToolLeveling {

    @SuppressWarnings("removal")
    public ForgeToolLeveling() {
        ForgePacketHandler.registerChannels();
        ModRegistry.load();

        MinecraftForge.EVENT_BUS.addListener(this::serverStartup);
        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);

        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::populateCreativeTab);
    }

    private void serverStartup(final ServerStartingEvent event) {
        ConfigManager.createConfigFolder();
        ConfigManager.loadAndVerifyConfigs();
    }

    public void registerCommands(final RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        SuperEnchantCommand.register(dispatcher, event.getBuildContext());
        ToolLevelingCommand.register(dispatcher);
    }

    private void populateCreativeTab(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ModRegistry.TLT_ITEM.get());
        }
    }

}
