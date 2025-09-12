package com.tristankechlo.toolleveling.platform;

import com.google.auto.service.AutoService;
import com.tristankechlo.toolleveling.blockentity.FabricBlockEntity;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.init.ModRegistry;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.function.Supplier;

@AutoService(IPlatformHelper.class)
public final class FabricPlatformHelper implements IPlatformHelper {

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Supplier<MenuType<ToolLevelingTableMenu>> buildContainer() {
        return () -> new ExtendedScreenHandlerType<>(ToolLevelingTableMenu::new);
    }

    @Override
    public Supplier<BlockEntityType<? extends ToolLevelingTableBlockEntity>> buildBlockEntityType() {
        return () -> FabricBlockEntityTypeBuilder.create(FabricBlockEntity::new, ModRegistry.TLT_BLOCK.get()).build();
    }

    @Override
    public ToolLevelingTableBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FabricBlockEntity(pos, state);
    }

    @Override
    public void openMenu(BlockState state, Level level, BlockPos pos, Player player) {
        MenuProvider provider = state.getMenuProvider(level, pos);
        if (provider != null) {
            player.openMenu(provider);
        }
    }

}
