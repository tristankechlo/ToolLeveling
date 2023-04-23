package com.tristankechlo.toolleveling.platform;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.FabricBlockEntity;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.function.Supplier;

public final class FabricPlatformHelper implements PlatformHelper {

    @Override
    public String getPlatformName() {
        return "Fabric";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Supplier<BlockEntityType<? extends ToolLevelingTableBlockEntity>> buildBlockEntityType() {
        return () -> FabricBlockEntityTypeBuilder.create(FabricBlockEntity::new, ToolLeveling.TLT_BLOCK.get()).build(null);
    }

    @Override
    public Supplier<MenuType<ToolLevelingTableMenu>> buildMenuType() {
        return () -> new ExtendedScreenHandlerType<>(ToolLevelingTableMenu::new);
    }

    @Override
    public ToolLevelingTableBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FabricBlockEntity(pos, state);
    }

}
