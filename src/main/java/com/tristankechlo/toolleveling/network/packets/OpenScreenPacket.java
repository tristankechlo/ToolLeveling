package com.tristankechlo.toolleveling.network.packets;

import java.util.function.Supplier;

import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class OpenScreenPacket {

	public OpenScreenPacket() {}

	public static void encode(OpenScreenPacket msg, PacketBuffer buffer) {}

	public static OpenScreenPacket decode(PacketBuffer buffer) {
		return new OpenScreenPacket();
	}

	public static void handle(OpenScreenPacket msg, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			Minecraft.getInstance().setScreen(new ItemValueScreen());
		});
		context.get().setPacketHandled(true);
	}

}
