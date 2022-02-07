package com.tristankechlo.toolleveling.network.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

public final class OpenItemValueScreenPacket {

	public OpenItemValueScreenPacket() {}

	public static void encode(OpenItemValueScreenPacket msg, FriendlyByteBuf buffer) {}

	public static OpenItemValueScreenPacket decode(FriendlyByteBuf buffer) {
		return new OpenItemValueScreenPacket();
	}

	public static void handle(OpenItemValueScreenPacket msg, Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientOpenItemValueScreenPacket.handle(msg, context));
		});
		context.get().setPacketHandled(true);
	}

}
