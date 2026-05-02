package com.tristankechlo.toolleveling;

import com.tristankechlo.toolleveling.client.blockentityrenderer.ToolLevelingTableRenderer;
import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableScreen;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.network.FabricClientPacketHandler;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screens.MenuScreens;

public class FabricClientSetup implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        FabricClientPacketHandler.registerPacketHandler();
        MenuScreens.register(ModRegistry.TLT_CONTAINER.get(), ToolLevelingTableScreen::new);
        BlockEntityRendererRegistry.register(ModRegistry.TLT_TILE_ENTITY.get(), ToolLevelingTableRenderer::new);
    }

}
