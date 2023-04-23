package com.tristankechlo.toolleveling.platform;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.nio.file.Path;
import java.util.function.Supplier;

public interface PlatformHelper {

    public static final PlatformHelper INSTANCE = Services.load(PlatformHelper.class);

    String getPlatformName();

    boolean isModLoaded(String modId);

    boolean isDevelopmentEnvironment();

    default String getEnvironmentName() {
        return isDevelopmentEnvironment() ? "development" : "production";
    }

    Path getConfigDirectory();

    Supplier<BlockEntityType<? extends ToolLevelingTableBlockEntity>> buildBlockEntityType();

    Supplier<MenuType<ToolLevelingTableMenu>> buildMenuType();

    ToolLevelingTableBlockEntity newBlockEntity(BlockPos pos, BlockState state);

}
