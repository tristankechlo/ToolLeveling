package com.tristankechlo.toolleveling.init;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.blocks.ToolLevelingTableBlock;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import com.tristankechlo.toolleveling.platform.IPlatformHelper;
import com.tristankechlo.toolleveling.platform.RegistrationProvider;
import com.tristankechlo.toolleveling.platform.RegistryObject;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.core.Registry;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

public final class ModRegistry {

    public static void load() {}

    public static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registry.BLOCK, Names.MOD_ID);
    public static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registry.ITEM, Names.MOD_ID);
    public static final RegistrationProvider<MenuType<?>> CONTAINER_TYPES = RegistrationProvider.get(Registry.MENU, Names.MOD_ID);
    public static final RegistrationProvider<BlockEntityType<?>> TILE_ENTITIES = RegistrationProvider.get(Registry.BLOCK_ENTITY_TYPE, Names.MOD_ID);

    private static final Properties std_properties = new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS).stacksTo(64);

    public static final RegistryObject<Block> TLT_BLOCK = BLOCKS.register(Names.TABLE, ToolLevelingTableBlock::new);

    public static final RegistryObject<Item> TLT_ITEM = ITEMS.register(Names.TABLE, () -> new BlockItem(TLT_BLOCK.get(), std_properties));

    public static final RegistryObject<MenuType<ToolLevelingTableMenu>> TLT_CONTAINER = CONTAINER_TYPES.register(Names.TABLE, IPlatformHelper.INSTANCE.buildContainer());

    public static final RegistryObject<BlockEntityType<? extends ToolLevelingTableBlockEntity>> TLT_TILE_ENTITY = TILE_ENTITIES.register(Names.TABLE, IPlatformHelper.INSTANCE.buildBlockEntityType());

}
