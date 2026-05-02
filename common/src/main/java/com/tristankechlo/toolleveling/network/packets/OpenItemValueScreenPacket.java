package com.tristankechlo.toolleveling.network.packets;

import net.minecraft.network.FriendlyByteBuf;

public record OpenItemValueScreenPacket() {

    public static void encode(OpenItemValueScreenPacket msg, FriendlyByteBuf buffer) {}

    public static OpenItemValueScreenPacket decode(FriendlyByteBuf buffer) {
        return new OpenItemValueScreenPacket();
    }

}
