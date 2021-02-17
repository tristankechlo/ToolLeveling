package com.tristankechlo.toolleveling.init;

import com.tristankechlo.toolleveling.blocks.ToolLevelingTableBlock;
import com.tristankechlo.toolleveling.container.ToolLevelingTableContainer;
import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRegistry {

	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Names.MOD_ID);
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Names.MOD_ID);
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, Names.MOD_ID);
	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, Names.MOD_ID);

	private static final Properties std_properties = new Item.Properties().group(ItemGroup.DECORATIONS).maxStackSize(64);

	public static final RegistryObject<Block> TLT_BLOCK = BLOCKS.register(Names.TABLE, () -> new ToolLevelingTableBlock());

	public static final RegistryObject<Item> TLT_ITEM = ITEMS.register(Names.TABLE, () -> new BlockItem(TLT_BLOCK.get(), std_properties));

	public static final RegistryObject<ContainerType<ToolLevelingTableContainer>> TLT_CONTAINER = CONTAINER_TYPES
			.register(Names.TABLE, () -> IForgeContainerType.create(ToolLevelingTableContainer::createForClientSide));

	public static final RegistryObject<TileEntityType<ToolLevelingTableTileEntity>> TLT_TILE_ENTITY = TILE_ENTITIES
			.register(Names.TABLE,() -> TileEntityType.Builder.create(ToolLevelingTableTileEntity::new, TLT_BLOCK.get()).build(null));
}
