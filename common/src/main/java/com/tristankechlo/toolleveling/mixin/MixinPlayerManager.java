package com.tristankechlo.toolleveling.mixin;

import com.tristankechlo.toolleveling.config.util.ConfigSyncing;
import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerList.class)
public class MixinPlayerManager {

    @Inject(at = @At(value = "TAIL"), method = "placeNewPlayer")
    private void onPlayerJoin(Connection connection, ServerPlayer player, CallbackInfo info) {
        ConfigSyncing.syncAllConfigsToOneClient(player);
    }

}
