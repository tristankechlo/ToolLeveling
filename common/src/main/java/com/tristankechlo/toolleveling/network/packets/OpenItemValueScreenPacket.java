package com.tristankechlo.toolleveling.network.packets;

import com.tristankechlo.toolleveling.client.screen.ItemValueScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;

public record OpenItemValueScreenPacket() {

    public static void encode(OpenItemValueScreenPacket msg, FriendlyByteBuf buffer) {}

    public static OpenItemValueScreenPacket decode(FriendlyByteBuf buffer) {
        return new OpenItemValueScreenPacket();
    }

    public static void handle(Minecraft minecraft) {
        minecraft.setScreen(new ItemValueScreen());
    }

}
