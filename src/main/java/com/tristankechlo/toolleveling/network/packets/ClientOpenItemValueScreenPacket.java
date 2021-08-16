package com.tristankechlo.toolleveling.network.packets;

import java.util.function.Supplier;

import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fmllegacy.network.NetworkEvent;

public class ClientOpenItemValueScreenPacket {

	public static void handle(OpenItemValueScreenPacket msg, Supplier<NetworkEvent.Context> context) {
		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
			Minecraft.getInstance().setScreen(new ItemValueScreen());
		});
	}

}
