package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingEnchantment;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
	
	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(ToolLeveling.MOD_ID, "main"),
			() -> PROTOCOL_VERSION,
			PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals
	);
	
	public static void registerPackets() {

        int id = 0;
        
        INSTANCE.registerMessage(id++, 
        		SyncToolLevelingEnchantment.class, 
        		SyncToolLevelingEnchantment::encode, 
        		SyncToolLevelingEnchantment::decode, 
        		SyncToolLevelingEnchantment::handle);
	}
}
