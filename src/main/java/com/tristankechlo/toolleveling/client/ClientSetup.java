package com.tristankechlo.toolleveling.client;

import com.tristankechlo.toolleveling.client.renderer.tile.ToolLevelingTableRenderer;
import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = Names.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

	public static void init(final FMLClientSetupEvent event) {
		ScreenManager.registerFactory(ModRegistry.TLT_CONTAINER.get(), ToolLevelingTableScreen::new);
		ClientRegistry.bindTileEntityRenderer(ModRegistry.TLT_TILE_ENTITY.get(), ToolLevelingTableRenderer::new);
	}
}
