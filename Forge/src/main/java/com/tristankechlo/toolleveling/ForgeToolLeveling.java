package com.tristankechlo.toolleveling;

import com.tristankechlo.toolleveling.commands.SuperEnchantCommand;
import com.tristankechlo.toolleveling.commands.ToolLevelingCommand;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.config.util.ConfigSyncingHelper;
import com.tristankechlo.toolleveling.network.ForgeNetworkHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ToolLeveling.MOD_ID)
public class ForgeToolLeveling {

    public ForgeToolLeveling() {
        ToolLeveling.init(); // register all items, blocks, etc.
        ForgeNetworkHelper.setup(); // register all packets

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(ForgeToolLevelingClient::init);
        modEventBus.addListener(this::populateCreativeTab);

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        //make sure config folder exists
        ConfigManager.createConfigFolder();
        //load configs from file
        ConfigManager.setup();
    }

    @SubscribeEvent
    public void onPlayerJoinEvent(final PlayerEvent.PlayerLoggedInEvent event) {
        // send server-config to player
        ConfigSyncingHelper.syncAllConfigsToOneClient((ServerPlayer) event.getEntity());
    }

    private void registerCommands(final RegisterCommandsEvent event) {
        ToolLevelingCommand.register(event.getDispatcher());
        SuperEnchantCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    private void populateCreativeTab(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ToolLeveling.TLT_ITEM.get());
        }
    }

}