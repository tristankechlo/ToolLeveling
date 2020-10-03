package com.tristankechlo.toolleveling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tristankechlo.toolleveling.client.ClientSetup;
import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import com.tristankechlo.toolleveling.init.ModBlocks;
import com.tristankechlo.toolleveling.init.ModContainers;
import com.tristankechlo.toolleveling.init.ModItems;
import com.tristankechlo.toolleveling.init.ModTileEntities;
import com.tristankechlo.toolleveling.network.ToolLevelingPacketHandler;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(ToolLeveling.MOD_ID)
public class ToolLeveling {
	
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "toolleveling";
	
	public ToolLeveling() {
		
    	ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ToolLevelingConfig.spec);
		
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		ToolLevelingPacketHandler.registerPackets();

		ModItems.ITEMS.register(modEventBus);
		ModBlocks.BLOCKS.register(modEventBus);
		ModTileEntities.TILE_ENTITIES.register(modEventBus);
		ModContainers.CONTAINER_TYPES.register(modEventBus);
		

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);
		
        
        MinecraftForge.EVENT_BUS.register(this);
        
	}
        
}
