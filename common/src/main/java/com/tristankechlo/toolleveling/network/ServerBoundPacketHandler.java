package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.enchantment.Enchantment;

public interface ServerBoundPacketHandler {

    ServerBoundPacketHandler INSTANCE = ToolLeveling.load(ServerBoundPacketHandler.class);

    void enchantAtToolLevelingTable(BlockPos pos, Enchantment enchantment, int level);

}
