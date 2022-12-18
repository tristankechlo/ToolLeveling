package com.tristankechlo.toolleveling.network.packets;

import java.util.function.Supplier;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.util.ConfigManager;

import com.tristankechlo.toolleveling.config.util.ConfigSyncing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public final class ClientSyncToolLevelingConfig {

	public static void handle(SyncToolLevelingConfig msg, Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			boolean success = ConfigSyncing.deserializeConfig(msg.identifier, msg.json);
			if (!success) {
				ToolLeveling.LOGGER.error("Config {} could not be loaded", msg.identifier);
				throw new RuntimeException("Config " + msg.identifier + " could not be loaded");
			} else {
				ToolLeveling.LOGGER.info("Config {} received and loaded.", msg.identifier);
			}
		});
	}

}
