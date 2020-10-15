package com.tristankechlo.toolleveling.init;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, ToolLeveling.MOD_ID);

	public static final RegistryObject<TileEntityType<ToolLevelingTableTileEntity>> TOOL_LEVELING_TABLE = TILE_ENTITIES
			.register("tool_leveling_table", () -> TileEntityType.Builder
					.create(ToolLevelingTableTileEntity::new, ModBlocks.TOOL_LEVELING_TABLE.get()).build(null));
}
