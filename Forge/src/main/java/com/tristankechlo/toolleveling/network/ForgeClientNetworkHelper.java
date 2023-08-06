package com.tristankechlo.toolleveling.network;

import com.tristankechlo.toolleveling.network.packets.TableUpgradeProcess;
import net.minecraft.core.BlockPos;

public class ForgeClientNetworkHelper implements ClientNetworkHelper {

    @Override
    public void startUpgradeProcess(BlockPos pos) {
        ForgeNetworkHelper.INSTANCE.sendToServer(new TableUpgradeProcess(pos));
    }

}
