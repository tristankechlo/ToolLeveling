package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public final class ForgeNetworkHelper implements NetworkHelper {

    @Override
    public void openMenu(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof ToolLevelingTableBlockEntity) {
            NetworkHooks.openScreen((ServerPlayer) player, (ToolLevelingTableBlockEntity) blockentity, buf -> {
                buf.writeBlockPos(pos);
            });
        }
    }

}
