package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import com.tristankechlo.toolleveling.network.packets.TableUpgradeProcess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ForgeNetworkHelper {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ToolLeveling.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void setup() {
        INSTANCE.registerMessage(0, TableUpgradeProcess.class,
                TableUpgradeProcess::encode, TableUpgradeProcess::decode,
                (msg, ctx) -> handleOnServer(msg, ctx, TableUpgradeProcess::handle));
        INSTANCE.registerMessage(1, SyncToolLevelingConfig.class,
                SyncToolLevelingConfig::encode, SyncToolLevelingConfig::decode,
                (msg, ctx) -> handleOnClient(msg, ctx, SyncToolLevelingConfig::handle));
    }

    /**
     * generic method to handle packets on the server,
     * unwraps the {@link ServerLevel} and calls the actual packet handler
     */
    private static <MSG> void handleOnServer(MSG msg, Supplier<NetworkEvent.Context> context, BiConsumer<MSG, ServerLevel> handler) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) {
                return;
            }
            handler.accept(msg, player.getLevel());
        });
        context.get().setPacketHandled(true);
    }

    /**
     * generic method to handle packets on the client,
     * makes sure the packet handler is called on the client and calls the actual packet handler
     */
    private static <MSG> void handleOnClient(MSG msg, Supplier<NetworkEvent.Context> context, Consumer<MSG> handler) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handler.accept(msg));
        });
        context.get().setPacketHandled(true);
    }

}
