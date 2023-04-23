package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.platform.Services;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

public interface NetworkHelper {

    public static final NetworkHelper INSTANCE = Services.load(NetworkHelper.class);

    void openMenu(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit);

}
