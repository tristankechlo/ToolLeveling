package com.tristankechlo.toolleveling.init;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.blocks.ToolLevelingTableBlock;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import com.tristankechlo.toolleveling.utils.Names;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModRegistry {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Names.MOD_ID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Names.MOD_ID);
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Names.MOD_ID);
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Names.MOD_ID);

    private static final Properties std_properties = new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS).stacksTo(64);

    public static final RegistryObject<Block> TLT_BLOCK = BLOCKS.register(Names.TABLE, () -> new ToolLevelingTableBlock());

    public static final RegistryObject<Item> TLT_ITEM = ITEMS.register(Names.TABLE, () -> new BlockItem(TLT_BLOCK.get(), std_properties));

    public static final RegistryObject<MenuType<ToolLevelingTableMenu>> TLT_CONTAINER = CONTAINER_TYPES.register(Names.TABLE, () -> IForgeMenuType.create(ToolLevelingTableMenu::new));

    public static final RegistryObject<BlockEntityType<ToolLevelingTableBlockEntity>> TLT_TILE_ENTITY = TILE_ENTITIES.register(Names.TABLE, () -> BlockEntityType.Builder.of(ToolLevelingTableBlockEntity::new, TLT_BLOCK.get()).build(null));

}
