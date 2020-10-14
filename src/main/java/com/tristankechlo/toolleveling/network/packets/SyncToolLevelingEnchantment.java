package com.tristankechlo.toolleveling.network.packets;

import java.util.Map;
import java.util.function.Supplier;

import com.tristankechlo.toolleveling.config.ToolLevelingConfig;
import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class SyncToolLevelingEnchantment {

	private final BlockPos pos;
	private final ResourceLocation enchantment;
	private final int level;
	
	public SyncToolLevelingEnchantment(BlockPos pos, ResourceLocation enchantment, int level) {
		this.pos = pos;
		this.enchantment = enchantment;
		this.level = level;
	}
	
	public static void encode (SyncToolLevelingEnchantment msg, PacketBuffer buffer) {
		buffer.writeBlockPos(msg.pos);
		buffer.writeResourceLocation(msg.enchantment);
		buffer.writeInt(msg.level);
	}
	
	public static SyncToolLevelingEnchantment decode (PacketBuffer buffer) {
		BlockPos pos = buffer.readBlockPos();
		ResourceLocation enchantment = buffer.readResourceLocation();
		int level = buffer.readInt();
		return new SyncToolLevelingEnchantment(pos, enchantment, level);
	}
	

	@SuppressWarnings("deprecation")
	public static void handle (SyncToolLevelingEnchantment msg, Supplier<NetworkEvent.Context> context) {
		
		context.get().enqueueWork(() -> {
			
			ServerPlayerEntity player = context.get().getSender();
			
			if(player != null) {
				
		        ServerWorld world = player.getServerWorld();
		        
		        if(world != null) {
		        	if(world.isBlockLoaded(msg.pos)){
		        		
		        		TileEntity entity = world.getTileEntity(msg.pos);
		        		if(entity != null && (entity instanceof ToolLevelingTableTileEntity)) {
		        			
		        			ToolLevelingTableTileEntity table = (ToolLevelingTableTileEntity)entity;
		        			ItemStack stack = table.getStackToEnchant();
		        			Enchantment targetEnchantment = ForgeRegistries.ENCHANTMENTS.getValue(msg.enchantment);
		        			Map<Enchantment, Integer> enchantmentsMap = EnchantmentHelper.getEnchantments(stack);
		        			int cost = (int)((10 + (msg.level-1)) * Math.max(0.0D, ToolLevelingConfig.SERVER.upgradeCostMultiplier.get()));
		        			if(enchantmentsMap.containsKey(targetEnchantment) && table.getReachableItems() >= cost) {
		        				if(table.removeItemsFromNearbyPillars(cost)) {
				        			enchantmentsMap.put(targetEnchantment, msg.level);
				        			EnchantmentHelper.setEnchantments(enchantmentsMap, stack);
				        			table.inventory.setStackInSlot(0, stack);
		        				}
		        			}
		        			table.markDirty();
		        		}
		        	}
		        }
			}	    
		});
	    context.get().setPacketHandled(true);
	}
	
}
