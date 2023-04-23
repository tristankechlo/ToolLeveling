package com.tristankechlo.toolleveling.platform;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.function.Supplier;

public class ForgePlatformHelper implements PlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }

    @Override
    public Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    @Override
    public Supplier<BlockEntityType<? extends ToolLevelingTableBlockEntity>> buildBlockEntityType() {
        return () -> BlockEntityType.Builder.of(ToolLevelingTableBlockEntity::new, ToolLeveling.TLT_BLOCK.get()).build(null);
    }

    @Override
    public Supplier<MenuType<ToolLevelingTableMenu>> buildMenuType() {
        return () -> IForgeMenuType.create(ToolLevelingTableMenu::new);
    }

    @Override
    public ToolLevelingTableBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ToolLevelingTableBlockEntity(pos, state);
    }

}
