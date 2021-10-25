package com.tristankechlo.toolleveling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tristankechlo.toolleveling.client.ClientSetup;
import com.tristankechlo.toolleveling.client.TooltipEvents;
import com.tristankechlo.toolleveling.config.ConfigManager;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.network.PacketHandler;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Names.MOD_ID)
public class ToolLeveling {

	public static final Logger LOGGER = LogManager.getLogger();

	public ToolLeveling() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		PacketHandler.registerPackets();

		ModRegistry.ITEMS.register(modEventBus);
		ModRegistry.BLOCKS.register(modEventBus);
		ModRegistry.TILE_ENTITIES.register(modEventBus);
		ModRegistry.CONTAINER_TYPES.register(modEventBus);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);

		MinecraftForge.EVENT_BUS.register(new TooltipEvents());
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		ConfigManager.setup();
	}

	@SubscribeEvent
	public void onPlayerJoinEvent(final PlayerLoggedInEvent event) {
		// send server-config to player
		ConfigManager.syncAllConfigsToOneClient((ServerPlayer) event.getPlayer());
	}

}
