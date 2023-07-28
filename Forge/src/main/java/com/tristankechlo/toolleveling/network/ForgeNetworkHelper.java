package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.network.packets.StartUpgradeProcess;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class ForgeNetworkHelper implements NetworkHelper {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ToolLeveling.MOD_ID, "main"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);

    public static void registerPackets() {
        INSTANCE.registerMessage(0, StartUpgradeProcess.class,
                StartUpgradeProcess::encode,
                StartUpgradeProcess::decode,
                (msg, ctx) -> handle(msg, ctx, StartUpgradeProcess::handle));
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
        INSTANCE.sendToServer(new StartUpgradeProcess(pos));
    }

    /**
     * generic method to handle packets,
     * unwraps the {@link ServerLevel} and calls the actual handler
     */
    static <MSG> void handle(MSG msg, Supplier<NetworkEvent.Context> context, BiConsumer<MSG, ServerLevel> handler) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) {
                return;
            }
            handler.accept(msg, player.getLevel());
        });
        context.get().setPacketHandled(true);
    }

}
