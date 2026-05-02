package com.tristankechlo.toolleveling.network;

import com.google.gson.JsonElement;
import com.tristankechlo.toolleveling.ToolLeveling;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;

public interface ClientBoundPacketHandler {

    ClientBoundPacketHandler INSTANCE = ToolLeveling.load(ClientBoundPacketHandler.class);

    void syncOneConfigToOneClient(ServerPlayer player, String identifier, JsonElement json);

    void syncOneConfigToAllClients(ServerLevel level, String identifier, JsonElement json);

    void openItemValueScreen(ServerPlayer player);

}
