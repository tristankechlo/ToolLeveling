package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.packets.TableUpgradeProcess;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class FabricNetworkHelper implements NetworkHelper {

    private static final ResourceLocation START_UPGRADE_PROCESS = new ResourceLocation(ToolLeveling.MOD_ID, "tool_leveling_table");

    public void registerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(START_UPGRADE_PROCESS,
                (server, player, listener, buf, sender) -> handle(server, player, buf, TableUpgradeProcess::decode, TableUpgradeProcess::handle));
    }

    @Override
    public void openMenu(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        MenuProvider provider = state.getMenuProvider(level, pos);
        if (provider != null) {
            player.openMenu(provider);
        }
    }

    @Override
    public void startUpgradeProcess(BlockPos pos) {
        FriendlyByteBuf buf = PacketByteBufs.create();
        TableUpgradeProcess.encode(buf, pos);
        ClientPlayNetworking.send(START_UPGRADE_PROCESS, buf);
    }

    /**
     * generic method to handle packets,
     * decodes the buffer and calls the actual packet handler
     */
    static <MSG> void handle(MinecraftServer server, ServerPlayer player, FriendlyByteBuf buf, PacketDecoder<MSG> decoder, PacketHandler<MSG> handler) {
        if (player == null) {
            return;
        }
        MSG msg = decoder.decode(buf);
        server.execute(() -> handler.handle(msg, player.getLevel()));
    }

}
