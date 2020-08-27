package com.tristankechlo.toolleveling.network.packets;

import java.util.Map;
import java.util.function.Supplier;

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
	        ServerWorld world = player.getServerWorld();
	        
	        if(world != null) {
	        	if(world.isBlockLoaded(msg.pos)){
	        		
	        		TileEntity entity = world.getTileEntity(msg.pos);
	        		if(entity instanceof ToolLevelingTableTileEntity) {
	        			
	        			ToolLevelingTableTileEntity table = (ToolLevelingTableTileEntity)entity;
	        			ItemStack stack = table.getInventory().getStackInSlot(0);
	        			Enchantment ench = ForgeRegistries.ENCHANTMENTS.getValue(msg.enchantment);
	        			Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
	        			enchantments.put(ench, msg.level);
	        			EnchantmentHelper.setEnchantments(enchantments, stack);
	        			table.inventory.setStackInSlot(0, stack);
	        			table.markDirty();
	        		}
	        	}
	        }
	    
		});
	    context.get().setPacketHandled(true);
	}
	
}
