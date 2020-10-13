package com.tristankechlo.toolleveling.init;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.tileentity.EnchantmentPillarTileEntity;
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

	public static final RegistryObject<TileEntityType<EnchantmentPillarTileEntity>> ENCHANTING_PILLAR = TILE_ENTITIES
			.register("enchanting_pillar", () -> TileEntityType.Builder
					.create(EnchantmentPillarTileEntity::new, ModBlocks.ENCHANTMENT_PILLAR.get()).build(null));
}
