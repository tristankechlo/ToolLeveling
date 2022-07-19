package com.tristankechlo.toolleveling;

import com.tristankechlo.toolleveling.client.blockentityrenderer.ToolLevelingTableRenderer;
import com.tristankechlo.toolleveling.client.screen.ToolLevelingTableHandledScreen;
import com.tristankechlo.toolleveling.network.ClientNetworkHandler;
import com.tristankechlo.toolleveling.utils.Names.NetworkChannels;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Environment(EnvType.CLIENT)
public final class ToolLevelingClient implements ClientModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();

	@Override
	public void onInitializeClient() {
		HandledScreens.register(ToolLeveling.TLT_SCREEN_HANDLER, ToolLevelingTableHandledScreen::new);
		BlockEntityRendererRegistry.register(ToolLeveling.TLT_BLOCK_ENTITY, ToolLevelingTableRenderer::new);

		// setup client packet reciever
		ClientPlayNetworking.registerGlobalReceiver(NetworkChannels.SYNC_CONFIG, ClientNetworkHandler::recieveSyncConfig);
		ClientPlayNetworking.registerGlobalReceiver(NetworkChannels.OPEN_ITEMVALUES, ClientNetworkHandler::recieveOpenItemValues);
	}

}
