package com.tristankechlo.toolleveling.network.packets;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class StartUpgradeProcess {

    private final BlockPos pos;

    public StartUpgradeProcess(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(StartUpgradeProcess msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static StartUpgradeProcess decode(FriendlyByteBuf buf) {
        return new StartUpgradeProcess(buf.readBlockPos());
    }

    public static void handle(StartUpgradeProcess msg, ServerLevel level) {
        if (level == null || !level.hasChunkAt(msg.pos)) {
            return;
        }
        BlockEntity entity = level.getBlockEntity(msg.pos);
        if ((entity instanceof ToolLevelingTableBlockEntity table)) {
            var o = WeightedRandom.getRandomItem(level.getRandom(), table.getEnchantments());
            if (o.isEmpty()) {
                return;
            }
            Enchantment e = o.get().getData();
            float successChance = Util.getSuccessChance(table.getItem(1), table.getItem(2), table.getItem(3));
            ToolLeveling.LOGGER.info("StartUpgradeProcess {} {}", successChance, e.getDescriptionId());
        }
    }

}
