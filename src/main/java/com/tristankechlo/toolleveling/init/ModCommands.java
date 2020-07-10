package com.tristankechlo.toolleveling.init;

import com.mojang.brigadier.CommandDispatcher;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.commands.TestCommand;

import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;

@Mod.EventBusSubscriber(modid = ToolLeveling.MOD_ID, bus = Bus.FORGE)
public class ModCommands {
	
	@SubscribeEvent
	public static void register(final FMLServerStartingEvent event) {
		final CommandDispatcher<CommandSource> dispatcher = event.getCommandDispatcher();
		TestCommand.register(dispatcher);
	}

}
