package com.tristankechlo.toolleveling.network;

import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import com.tristankechlo.toolleveling.network.packets.TableUpgradeProcess;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public final class ForgeNetworkHelper implements NetworkHelper {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ToolLeveling.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    @Override
    public void registerPackets() {
        INSTANCE.registerMessage(0, TableUpgradeProcess.class,
                (msg, buf) -> TableUpgradeProcess.encode(buf, msg.pos()),
                TableUpgradeProcess::decode,
                (msg, ctx) -> handleOnServer(msg, ctx, TableUpgradeProcess::handle));
        INSTANCE.registerMessage(1, SyncToolLevelingConfig.class,
                (msg, buf) -> SyncToolLevelingConfig.encode(buf, msg.identifier(), msg.json()),
                SyncToolLevelingConfig::decode,
                (msg, ctx) -> handleOnClient(msg, ctx, SyncToolLevelingConfig::handle));
    }

    @Override
    public void openMenu(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof ToolLevelingTableBlockEntity) {
            NetworkHooks.openScreen((ServerPlayer) player, (ToolLevelingTableBlockEntity) blockentity, buf -> {
                buf.writeBlockPos(pos);
            });
        }
    }

    @Override
    public void startUpgradeProcess(BlockPos pos) {
        INSTANCE.sendToServer(new TableUpgradeProcess(pos));
    }

    @Override
    public void syncToolLevelingConfig(ServerPlayer player, String identifier, JsonObject json) {
        Connection connection = player.connection.connection;
        INSTANCE.sendTo(new SyncToolLevelingConfig(identifier, json), connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public void syncToolLevelingConfigToAllClients(MinecraftServer server, String identifier, JsonObject json) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncToolLevelingConfig(identifier, json));
    }

    /**
     * generic method to handle packets on the server,
     * unwraps the {@link ServerLevel} and calls the actual packet handler
     */
    private static <MSG> void handleOnServer(MSG msg, Supplier<NetworkEvent.Context> context, ServerSidePacketHandler<MSG> handler) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) {
                return;
            }
            handler.handle(msg, player.getLevel());
        });
        context.get().setPacketHandled(true);
    }

    /**
     * generic method to handle packets on the client,
     * makes sure the packet handler is called on the client and calls the actual packet handler
     */
    private static <MSG> void handleOnClient(MSG msg, Supplier<NetworkEvent.Context> context, ClientSidePacketHandler<MSG> handler) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handler.handle(msg));
        });
        context.get().setPacketHandled(true);
    }

}
