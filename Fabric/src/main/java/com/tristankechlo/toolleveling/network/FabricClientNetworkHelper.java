package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import com.tristankechlo.toolleveling.network.packets.TableUpgradeProcess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.Consumer;
import java.util.function.Function;

public class FabricClientNetworkHelper implements ClientNetworkHelper {

    @Override
    public void registerPacketReceiver() {
        ClientPlayNetworking.registerGlobalReceiver(SyncToolLevelingConfig.CHANNEL_ID,
                (client, listener, buf, sender) -> handleOnClient(client, buf, SyncToolLevelingConfig::decode, SyncToolLevelingConfig::handle));
    }

    @Override
    public void startUpgradeProcess(BlockPos pos) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        TableUpgradeProcess.encode(buf, pos);
        ClientPlayNetworking.send(TableUpgradeProcess.CHANNEL_ID, buf);
    }

    /**
     * generic method to handle packets on the client,
     * decodes the buffer and calls the actual packet handler
     */
    private static <MSG> void handleOnClient(Minecraft client, FriendlyByteBuf buf, Function<FriendlyByteBuf, MSG> decoder, Consumer<MSG> handler) {
        if (client == null) {
            return;
        }
        MSG msg = decoder.apply(buf);
        client.execute(() -> handler.accept(msg));
    }

}
