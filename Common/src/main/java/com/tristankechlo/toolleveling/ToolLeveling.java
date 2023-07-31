package com.tristankechlo.toolleveling;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.blocks.ToolLevelingTableBlock;
import com.tristankechlo.toolleveling.menu.ToolLevelingTableMenu;
import com.tristankechlo.toolleveling.platform.PlatformHelper;
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

    // urls
    public static final String GITHUB = "https://github.com/tristankechlo/ToolLeveling";
    public static final String GITHUB_ISSUE = GITHUB + "/issues";
    public static final String GITHUB_WIKI = GITHUB + "/wiki";
    public static final String DISCORD = "https://discord.gg/bhUaWhq";
    public static final String CURSEFORGE = "https://curseforge.com/minecraft/mc-mods/tool-leveling-plus";
    public static final String MODRINTH = "https://modrinth.com/mod/tool-leveling";
    public static final String CONFIG_START = "https://github.com/tristankechlo/ToolLeveling/wiki/General-Information-to-the-ToolLeveling-Configs";
    public static final String CONFIG_INFO_GENERAL = "https://github.com/tristankechlo/ToolLeveling/wiki/Config-tool_leveling_table.json";
    public static final String CONFIG_INFO_COMMANDS = "https://github.com/tristankechlo/ToolLeveling/wiki/Config-command_config.json";

    // registries
    private static final RegistrationProvider<Block> BLOCKS = RegistrationProvider.get(Registries.BLOCK, MOD_ID);
    private static final RegistrationProvider<Item> ITEMS = RegistrationProvider.get(Registries.ITEM, MOD_ID);
    private static final RegistrationProvider<BlockEntityType<?>> BLOCK_ENTITIES = RegistrationProvider.get(Registries.BLOCK_ENTITY_TYPE, MOD_ID);
    private static final RegistrationProvider<MenuType<?>> MENU_TYPES = RegistrationProvider.get(Registries.MENU, MOD_ID);

    // registry objects
    public static final RegistryObject<Block> TLT_BLOCK = BLOCKS.register(TABLE_NAME, ToolLevelingTableBlock::new);
    public static final RegistryObject<Item> TLT_ITEM = ITEMS.register(TABLE_NAME, () -> new BlockItem(TLT_BLOCK.get(), new Item.Properties()));
    public static final RegistryObject<BlockEntityType<? extends ToolLevelingTableBlockEntity>> TLT_BLOCK_ENTITY = BLOCK_ENTITIES.register(TABLE_NAME, PlatformHelper.INSTANCE.buildBlockEntityType());
    public static final RegistryObject<MenuType<ToolLevelingTableMenu>> TLT_MENU = MENU_TYPES.register(TABLE_NAME, PlatformHelper.INSTANCE.buildMenuType());

    public static void init() {
        LOGGER.info("Initializing " + MOD_NAME + "...");
    }

}