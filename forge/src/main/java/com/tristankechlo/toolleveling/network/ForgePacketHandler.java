package com.tristankechlo.toolleveling.network;

import com.google.auto.service.AutoService;
import com.google.gson.JsonElement;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.network.packets.OpenItemValueScreenPacket;
import com.tristankechlo.toolleveling.network.packets.SetEnchantmentToolLevelingTable;
import com.tristankechlo.toolleveling.network.packets.SyncToolLevelingConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

@SuppressWarnings("removal") // for ResourceLocation 1.20.6+
@AutoService({ServerBoundPacketHandler.class, ClientBoundPacketHandler.class})
public final class ForgePacketHandler implements ServerBoundPacketHandler, ClientBoundPacketHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ToolLeveling.MOD_ID, "main"), () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals);

    public static void registerChannels() {
        INSTANCE.registerMessage(0,
                SetEnchantmentToolLevelingTable.class,
                SetEnchantmentToolLevelingTable::encode,
                SetEnchantmentToolLevelingTable::decode,
                ForgePacketHandler::handleEnchantAtToolLevelingTable);

        INSTANCE.registerMessage(1,
                SyncToolLevelingConfig.class,
                SyncToolLevelingConfig::encode,
                SyncToolLevelingConfig::decode,
                ForgePacketHandler::handleConfigSync);

        INSTANCE.registerMessage(2,
                OpenItemValueScreenPacket.class,
                OpenItemValueScreenPacket::encode,
                OpenItemValueScreenPacket::decode,
                ForgePacketHandler::handleOpenItemValues);
    }

    // ####################
    // SENDING PACKETS
    // ####################

    @Override
    public void enchantAtToolLevelingTable(BlockPos pos, Enchantment enchantment, int level) {
        INSTANCE.sendToServer(new SetEnchantmentToolLevelingTable(pos, enchantment, level));
    }

    @Override
    public void syncOneConfigToOneClient(ServerPlayer player, String identifier, JsonElement json) {
        Connection connection = player.connection.getConnection();
        INSTANCE.sendTo(new SyncToolLevelingConfig(identifier, json), connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    @Override
    public void syncOneConfigToAllClients(ServerLevel level, String identifier, JsonElement json) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), new SyncToolLevelingConfig(identifier, json));
    }

    @Override
    public void openItemValueScreen(ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new OpenItemValueScreenPacket());
    }

    // ####################
    // HANDLING PACKETS
    // ####################

    static void handleEnchantAtToolLevelingTable(SetEnchantmentToolLevelingTable msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayer player = context.get().getSender();
            if (player == null) {
                ToolLeveling.LOGGER.warn("Received SetEnchantmentToolLevelingTable packet from invalid player");
                return;
            }
            SetEnchantmentToolLevelingTable.handle(msg, player, (ServerLevel) player.level);
        });
        context.get().setPacketHandled(true);
    }

    static void handleConfigSync(SyncToolLevelingConfig msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> SyncToolLevelingConfig.handle(msg));
        });
        context.get().setPacketHandled(true);
    }

    static void handleOpenItemValues(OpenItemValueScreenPacket msg, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> ClientOpenItemValueScreenPacket.handle(msg, context));
        });
        context.get().setPacketHandled(true);
    }

}
