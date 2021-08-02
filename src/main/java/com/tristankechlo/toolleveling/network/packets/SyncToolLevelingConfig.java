package com.tristankechlo.toolleveling.network.packets;

import java.util.function.Supplier;

import org.apache.logging.log4j.Level;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.config.ConfigManager;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SyncToolLevelingConfig {

	private final String identifier;
	private final JsonObject json;

	public SyncToolLevelingConfig(String identifier, JsonObject json) {
		this.identifier = identifier;
		this.json = json;
	}

	public static void encode(SyncToolLevelingConfig msg, PacketBuffer buffer) {
		buffer.writeUtf(msg.identifier);
		buffer.writeUtf(new Gson().toJson(msg.json));
	}

	public static SyncToolLevelingConfig decode(PacketBuffer buffer) {
		String identifier = buffer.readUtf();
		JsonObject json = new JsonParser().parse(buffer.readUtf()).getAsJsonObject();
		return new SyncToolLevelingConfig(identifier, json);
	}

	public static void handle(SyncToolLevelingConfig msg, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			boolean check = ConfigManager.deserializeConfig(msg.identifier, msg.json);
			if (!check) {
				ToolLeveling.LOGGER.log(Level.ERROR, "Config " + msg.identifier + " could not be loaded");
				throw new RuntimeException("Config " + msg.identifier + " could not be loaded");
			} else {
				ToolLeveling.LOGGER.log(Level.INFO, "Config " + msg.identifier + " recieved and loaded.");
			}
		});
		context.get().setPacketHandled(true);
	}
}
