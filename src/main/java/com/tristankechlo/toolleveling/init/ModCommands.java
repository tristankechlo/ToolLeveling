package com.tristankechlo.toolleveling.init;

import com.mojang.brigadier.CommandDispatcher;
import com.tristankechlo.toolleveling.ToolLeveling;
import com.tristankechlo.toolleveling.commands.SuperEnchantCommand;

import net.minecraft.command.CommandSource;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = ToolLeveling.MOD_ID, bus = Bus.FORGE)
public class ModCommands {
	
	@SubscribeEvent
	public static void register(final RegisterCommandsEvent event) {
		final CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();
		SuperEnchantCommand.register(dispatcher);
	}

}
