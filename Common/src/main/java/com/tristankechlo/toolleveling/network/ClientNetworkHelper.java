package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.platform.Services;
import net.minecraft.core.BlockPos;

public interface ClientNetworkHelper {

    ClientNetworkHelper INSTANCE = Services.load(ClientNetworkHelper.class);

    static void setup() {
        INSTANCE.registerPacketReceiver();
        ToolLeveling.LOGGER.info("ClientSidePacketHandler registered");
    }

    default void registerPacketReceiver() {}

    /**
     * Sends a packet to the server to start the upgrade process
     */
    void startUpgradeProcess(BlockPos pos);

}
