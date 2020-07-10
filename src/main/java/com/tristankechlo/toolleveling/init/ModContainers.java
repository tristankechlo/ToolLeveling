package com.tristankechlo.toolleveling.init;

import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.container.ToolLevelingTableContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {
	
	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, ToolLeveling.MOD_ID);
	
	public static final RegistryObject<ContainerType<ToolLevelingTableContainer>> TOOL_LEVELING_TABLE = 
			CONTAINER_TYPES.register("tool_leveling_table", () -> IForgeContainerType.create(ToolLevelingTableContainer::new));
	
}
