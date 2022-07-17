package com.tristankechlo.toolleveling.network.packets;

import java.util.function.Supplier;

import com.tristankechlo.toolleveling.ToolLeveling;

import com.tristankechlo.toolleveling.config.util.ConfigSyncing;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public final class ClientSyncToolLevelingConfig {

	public static void handle(SyncToolLevelingConfig msg, Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			boolean check = ConfigSyncing.deserializeConfig(msg.identifier, msg.json);
			if (!check) {
				ToolLeveling.LOGGER.error("Config " + msg.identifier + " could not be loaded");
				throw new RuntimeException("Config " + msg.identifier + " could not be loaded");
			} else {
				ToolLeveling.LOGGER.info("Config " + msg.identifier + " recieved and loaded.");
			}
		});
	}

}
