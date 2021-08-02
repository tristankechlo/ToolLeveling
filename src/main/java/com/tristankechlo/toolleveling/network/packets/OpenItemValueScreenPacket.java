package com.tristankechlo.toolleveling.network.packets;

import java.util.function.Supplier;

import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenItemValueScreenPacket {

	public OpenItemValueScreenPacket() {}

	public static void encode(OpenItemValueScreenPacket msg, PacketBuffer buffer) {}

	public static OpenItemValueScreenPacket decode(PacketBuffer buffer) {
		return new OpenItemValueScreenPacket();
	}

	public static void handle(OpenItemValueScreenPacket msg, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			Minecraft.getInstance().setScreen(new ItemValueScreen());
		});
		context.get().setPacketHandled(true);
	}

}
