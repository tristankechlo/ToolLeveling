package com.tristankechlo.toolleveling;

import com.tristankechlo.toolleveling.client.ToolLevelingTableRenderer;
import com.tristankechlo.toolleveling.client.ToolLevelingTableScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ToolLeveling.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ForgeToolLevelingClient {

    public static void init(final FMLClientSetupEvent event) {
        MenuScreens.register(ToolLeveling.TLT_MENU.get(), ToolLevelingTableScreen::new);
        BlockEntityRenderers.register(ToolLeveling.TLT_BLOCK_ENTITY.get(), ToolLevelingTableRenderer::new);
    }

}
