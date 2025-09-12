package com.tristankechlo.toolleveling.network;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;

public interface IPacketHandler {

    IPacketHandler INSTANCE = ToolLeveling.load(IPacketHandler.class);

    void enchantAtToolLevelingTable(BlockPos pos, Enchantment enchantment, int level);

    void syncOneConfigToOneClient(ServerPlayer player, String identifier, JsonElement json);

    void syncOneConfigToAllClients(ServerLevel level, String identifier, JsonElement json);

    void openItemValueScreen(ServerPlayer player);

}
