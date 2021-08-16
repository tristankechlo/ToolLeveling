package com.tristankechlo.toolleveling.network.packets;

import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.ConfigManager;

import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ClientSyncToolLevelingConfig {

	public static void handle(SyncToolLevelingConfig msg, Supplier<NetworkEvent.Context> context) {
		boolean check = ConfigManager.deserializeConfig(msg.identifier, msg.json);
		if (!check) {
			ToolLeveling.LOGGER.log(Level.ERROR, "Config " + msg.identifier + " could not be loaded");
			throw new RuntimeException("Config " + msg.identifier + " could not be loaded");
		} else {
			ToolLeveling.LOGGER.log(Level.INFO, "Config " + msg.identifier + " recieved and loaded.");
		}
	}

}
