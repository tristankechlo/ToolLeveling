package com.tristankechlo.toolleveling.network.packets;

import java.util.function.Supplier;

import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class OpenItemValueScreenPacket {

	public OpenItemValueScreenPacket() {}

	public static void encode(OpenItemValueScreenPacket msg, FriendlyByteBuf buffer) {}

	public static OpenItemValueScreenPacket decode(FriendlyByteBuf buffer) {
		return new OpenItemValueScreenPacket();
	}

	public static void handle(OpenItemValueScreenPacket msg, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			Minecraft.getInstance().setScreen(new ItemValueScreen());
		});
		context.get().setPacketHandled(true);
	}

}
