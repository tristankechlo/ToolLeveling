package com.tristankechlo.toolleveling;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;
import com.tristankechlo.toolleveling.blocks.ToolLevelingTableBlock;
import com.tristankechlo.toolleveling.commands.ConfigIdentifierArgumentType;
import com.tristankechlo.toolleveling.commands.SuperEnchantCommand;
import com.tristankechlo.toolleveling.commands.ToolLevelingCommand;
import com.tristankechlo.toolleveling.config.ConfigManager;
import com.tristankechlo.toolleveling.network.ServerNetworkHandler;
import com.tristankechlo.toolleveling.screenhandler.ToolLevelingTableScreenhandler;
import com.tristankechlo.toolleveling.utils.Names;
import com.tristankechlo.toolleveling.utils.Names.NetworkChannels;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.command.argument.ArgumentTypes;
import net.minecraft.command.argument.serialize.ArgumentSerializer;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public final class ToolLeveling implements ModInitializer {

	public static final Logger LOGGER = LogManager.getLogger();
	public static final Identifier TOOLLEVELING_TABLE_ID = new Identifier(Names.MOD_ID, Names.TABLE);
	public static final Block TLT_BLOCK = new ToolLevelingTableBlock();
	public static BlockEntityType<ToolLevelingTableBlockEntity> TLT_BLOCK_ENTITY;
	public static final ScreenHandlerType<ToolLevelingTableScreenhandler> TLT_SCREEN_HANDLER;

	static {
		TLT_SCREEN_HANDLER = new ExtendedScreenHandlerType<>(ToolLevelingTableScreenhandler::new);
	}

	@Override
	public void onInitialize() {
		// register serverside packet reciever
		ServerPlayNetworking.registerGlobalReceiver(NetworkChannels.SET_ENCHANTMENT_LEVEL,
				ServerNetworkHandler::recieveSetEnchantmentLevel);

		// register item/block and blockentity
		Registry.register(Registry.BLOCK, TOOLLEVELING_TABLE_ID, TLT_BLOCK);
		Registry.register(Registry.ITEM, TOOLLEVELING_TABLE_ID,
				new BlockItem(TLT_BLOCK, new FabricItemSettings().group(ItemGroup.DECORATIONS).maxCount(64)));
		TLT_BLOCK_ENTITY = Registry.register(Registry.BLOCK_ENTITY_TYPE, TOOLLEVELING_TABLE_ID,
				FabricBlockEntityTypeBuilder.create(ToolLevelingTableBlockEntity::new, TLT_BLOCK).build(null));
		Registry.register(Registry.SCREEN_HANDLER, TOOLLEVELING_TABLE_ID, TLT_SCREEN_HANDLER);

		// register custom argument type
		ArgumentSerializer<?, ?> serializer = ConstantArgumentSerializer.of(ConfigIdentifierArgumentType::get);
		ArgumentTypes.CLASS_MAP.put(ConfigIdentifierArgumentType.class, serializer);
		Registry.register(Registry.COMMAND_ARGUMENT_TYPE, Names.MOD_ID + ":config_indentifier", serializer);

		// register commands
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
			ToolLevelingCommand.register(dispatcher);
			SuperEnchantCommand.register(dispatcher);
		});

		// server start evet
		ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
			ConfigManager.setup();
		});
	}

}
