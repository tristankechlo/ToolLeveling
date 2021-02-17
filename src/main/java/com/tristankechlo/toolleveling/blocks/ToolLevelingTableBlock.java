package com.tristankechlo.toolleveling.blocks;

import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;

public class ToolLevelingTableBlock extends ContainerBlock {

	private static final VoxelShape SHAPE = VoxelShapes.or(makeCuboidShape(2, 0, 2, 14, 3, 14),
			makeCuboidShape(3, 3, 3, 13, 6, 13), makeCuboidShape(4, 6, 4, 12, 11, 12),
			makeCuboidShape(3, 11, 3, 13, 14, 13));

	public ToolLevelingTableBlock() {
		super(Block.Properties.create(Material.IRON, MaterialColor.GRAY)
				.hardnessAndResistance(4.5f, 1000.0f)
				.sound(SoundType.METAL)
				.harvestLevel(2)
				.harvestTool(ToolType.PICKAXE)
				.notSolid()
				.setRequiresTool());
		this.setDefaultState(this.getDefaultState());
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.IGNORE;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player,
			Hand hand, BlockRayTraceResult hit) {
		if (world.isRemote) {
			return ActionResultType.SUCCESS;
		}
		INamedContainerProvider namedContainerProvider = this.getContainer(state, world, pos);
		if (namedContainerProvider != null) {
			if (!(player instanceof ServerPlayerEntity)) {
				return ActionResultType.FAIL;
			}
			ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
			NetworkHooks.openGui(serverPlayerEntity, namedContainerProvider, (buffer) -> {
				buffer.writeBlockPos(pos);
			});
		}
		return ActionResultType.SUCCESS;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = world.getTileEntity(pos);
			if (tileentity instanceof ToolLevelingTableTileEntity) {
				ToolLevelingTableTileEntity table = (ToolLevelingTableTileEntity) tileentity;
				table.dropAllContents(world, pos);
			}
			super.onReplaced(state, world, pos, newState, isMoving);
		}
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return createNewTileEntity(world);
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new ToolLevelingTableTileEntity();
	}

}
