package com.tristankechlo.toolleveling.network;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public final class FabricNetworkHelper implements NetworkHelper {

    @Override
    public void openMenu(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        MenuProvider provider = state.getMenuProvider(level, pos);
        if (provider != null) {
            player.openMenu(provider);
        }
    }

}
