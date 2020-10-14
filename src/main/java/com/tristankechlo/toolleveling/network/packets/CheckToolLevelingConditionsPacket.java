package com.tristankechlo.toolleveling.network.packets;

import java.util.function.Supplier;

import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class CheckToolLevelingConditionsPacket {

	private final BlockPos pos;
	
	public CheckToolLevelingConditionsPacket(BlockPos pos) {
		this.pos = pos;
	}
	
	public static void encode (CheckToolLevelingConditionsPacket msg, PacketBuffer buffer) {
		buffer.writeBlockPos(msg.pos);
	}
	
	public static CheckToolLevelingConditionsPacket decode (PacketBuffer buffer) {
		BlockPos pos = buffer.readBlockPos();
		return new CheckToolLevelingConditionsPacket(pos);
	}
	

	@SuppressWarnings("deprecation")
	public static void handle (CheckToolLevelingConditionsPacket msg, Supplier<NetworkEvent.Context> context) {
		
		context.get().enqueueWork(() -> {
			
			ServerPlayerEntity player = context.get().getSender();
			
			if(player != null) {
				
		        ServerWorld world = player.getServerWorld();
		        
		        if(world != null) {
		        	if(world.isBlockLoaded(msg.pos)){
		        		
		        		TileEntity entity = world.getTileEntity(msg.pos);
		        		if(entity != null && (entity instanceof ToolLevelingTableTileEntity)) {
		        			
		        			ToolLevelingTableTileEntity tileEntity = (ToolLevelingTableTileEntity)entity;
		        			
		        			tileEntity.markDirty();
		        		}
		        	}
		        }
			}
		});
	    context.get().setPacketHandled(true);
	}
}
