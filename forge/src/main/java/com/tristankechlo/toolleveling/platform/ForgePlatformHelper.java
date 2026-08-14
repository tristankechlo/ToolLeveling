package com.tristankechlo.toolleveling.platform;

import com.google.auto.service.AutoService;
import com.tristankechlo.toolleveling.blockentity.ForgeBlockEntity;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkHooks;

import java.nio.file.Path;
import java.util.function.Supplier;

@AutoService(IPlatformHelper.class)
public final class ForgePlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Supplier<MenuType<ToolLevelingTableMenu>> buildContainer() {
        return () -> IForgeMenuType.create(ToolLevelingTableMenu::new);
    }

    @Override
    public Supplier<BlockEntityType<? extends ToolLevelingTableBlockEntity>> buildBlockEntityType() {
        return () -> BlockEntityType.Builder.of(ForgeBlockEntity::new, ModRegistry.TLT_BLOCK.get()).build(null);
    }

    @Override
    public ToolLevelingTableBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ForgeBlockEntity(pos, state);
    }

    @Override
    public void openMenu(BlockState state, Level level, BlockPos pos, Player player) {
        BlockEntity blockentity = level.getBlockEntity(pos);
        if (blockentity instanceof ToolLevelingTableBlockEntity) {
            NetworkHooks.openScreen((ServerPlayer) player, (ToolLevelingTableBlockEntity) blockentity, buf -> buf.writeBlockPos(pos));
        }
    }

}
