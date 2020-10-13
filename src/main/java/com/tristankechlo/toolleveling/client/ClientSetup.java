package com.tristankechlo.toolleveling.client;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.client.gui.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.client.renderer.tile.EnchantmentPillarRenderer;
import com.tristankechlo.toolleveling.init.ModContainers;
import com.tristankechlo.toolleveling.init.ModTileEntities;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ToolLeveling.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    public static void init(final FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContainers.TOOL_LEVELING_TABLE.get(), ToolLevelingTableScreen::new);
		ClientRegistry.bindTileEntityRenderer(ModTileEntities.ENCHANTING_PILLAR.get(), EnchantmentPillarRenderer::new);
    }
}
