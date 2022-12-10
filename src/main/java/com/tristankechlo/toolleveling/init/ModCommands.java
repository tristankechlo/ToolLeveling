package com.tristankechlo.toolleveling.init;

import com.mojang.brigadier.CommandDispatcher;
import com.tristankechlo.toolleveling.commands.SuperEnchantCommand;
import com.tristankechlo.toolleveling.commands.ToolLevelingCommand;
import com.tristankechlo.toolleveling.utils.Names;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Names.MOD_ID, bus = Bus.FORGE)
public final class ModCommands {

	@SubscribeEvent
	public static void register(final RegisterCommandsEvent event) {
		final CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
		final CommandBuildContext context = event.getBuildContext();
		SuperEnchantCommand.register(dispatcher, context);
		ToolLevelingCommand.register(dispatcher);
	}

}
