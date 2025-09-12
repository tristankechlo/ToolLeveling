package com.tristankechlo.toolleveling.blockentity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.state.BlockState;

public class FabricBlockEntity extends ToolLevelingTableBlockEntity implements ExtendedScreenHandlerFactory {

    public FabricBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public void writeScreenOpeningData(ServerPlayer player, FriendlyByteBuf buf) {
        buf.writeBlockPos(this.worldPosition);
    }

}
