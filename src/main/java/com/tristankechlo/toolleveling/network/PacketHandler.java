package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.network.packets.OpenItemValueScreenPacket;
import com.tristankechlo.toolleveling.network.packets.SetEnchantmentToolLevelingTable;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {

	private static final String PROTOCOL_VERSION = "1";
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
			new ResourceLocation(Names.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
			PROTOCOL_VERSION::equals);

	public static void registerPackets() {

		int id = 0;

		INSTANCE.registerMessage(id++,
				SetEnchantmentToolLevelingTable.class,
				SetEnchantmentToolLevelingTable::encode,
				SetEnchantmentToolLevelingTable::decode,
				SetEnchantmentToolLevelingTable::handle);

		INSTANCE.registerMessage(id++,
				SyncToolLevelingConfig.class,
				SyncToolLevelingConfig::encode,
				SyncToolLevelingConfig::decode,
				SyncToolLevelingConfig::handle);

		INSTANCE.registerMessage(id++,
				OpenItemValueScreenPacket.class,
				OpenItemValueScreenPacket::encode,
				OpenItemValueScreenPacket::decode,
				OpenItemValueScreenPacket::handle);
	}
}
