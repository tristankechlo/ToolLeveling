package com.tristankechlo.toolleveling;

import com.tristankechlo.toolleveling.blocks.ToolLevelingTableBlock;
import com.tristankechlo.toolleveling.platform.RegistrationProvider;
import com.tristankechlo.toolleveling.platform.RegistryObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ToolLeveling {

    public static final String MOD_ID = "toolleveling";
    public static final String MOD_NAME = "ToolLeveling+";
    public static final String TABLE_NAME = "tool_leveling_table";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    private static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK, MOD_ID);
    private static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, MOD_ID);
    private static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    private static final RegistrationProvider<MenuType<?>> MENU_TYPES = RegistrationProvider.get(Registries.MENU, MOD_ID);

    public static final RegistryObject<Block> TLT_BLOCK = BLOCKS.register(TABLE_NAME, ToolLevelingTableBlock::new);
    public static final RegistryObject<Item> TLT_ITEM = ITEMS.register(TABLE_NAME, () -> new BlockItem(TLT_BLOCK.get(), new Item.Properties()));

    public static void init() {
    }

}