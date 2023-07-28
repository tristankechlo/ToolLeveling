package com.tristankechlo.toolleveling;

import com.tristankechlo.toolleveling.commands.SuperEnchantCommand;
import com.tristankechlo.toolleveling.network.NetworkHelper;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ToolLeveling.MOD_ID)
public class ForgeToolLeveling {

    public ForgeToolLeveling() {
        ToolLeveling.init(); // register all items, blocks, etc.
        NetworkHelper.setup(); // register all packets

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(ForgeToolLevelingClient::init);
        modEventBus.addListener(this::populateCreativeTab);

        MinecraftForge.EVENT_BUS.addListener(this::registerCommands);
        MinecraftForge.EVENT_BUS.register(this);
    }


    private void registerCommands(final RegisterCommandsEvent event) {
        SuperEnchantCommand.register(event.getDispatcher(), event.getBuildContext());
    }

    private void populateCreativeTab(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.accept(ToolLeveling.TLT_ITEM.get());
        }
    }

}