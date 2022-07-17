package com.tristankechlo.toolleveling;

import com.mojang.logging.LogUtils;
import com.tristankechlo.toolleveling.client.ClientSetup;
import com.tristankechlo.toolleveling.config.util.ConfigManager;
import com.tristankechlo.toolleveling.config.util.ConfigSyncing;
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
import org.slf4j.Logger;

@Mod(Names.MOD_ID)
public final class ToolLeveling {

    public static final Logger LOGGER = LogUtils.getLogger();

	public ToolLeveling() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		PacketHandler.registerPackets();

		ModRegistry.ITEMS.register(modEventBus);
		ModRegistry.BLOCKS.register(modEventBus);
		ModRegistry.TILE_ENTITIES.register(modEventBus);
		ModRegistry.CONTAINER_TYPES.register(modEventBus);

		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
		FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientSetup::init);

		MinecraftForge.EVENT_BUS.register(this);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		ConfigManager.setup();
	}

	@SubscribeEvent
	public void onPlayerJoinEvent(final PlayerLoggedInEvent event) {
		// send server-config to player
		ConfigSyncing.syncAllConfigsToOneClient((ServerPlayer) event.getEntity());
	}

}
