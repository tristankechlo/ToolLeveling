package com.tristankechlo.toolleveling.mixin;

import com.tristankechlo.toolleveling.config.util.ConfigSyncing;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {

    @Inject(at = @At(value = "TAIL"), method = "onPlayerConnect", cancellable = true)
    private void onPlayerJoin(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        ConfigSyncing.syncAllConfigsToOneClient(player);
    }

}
