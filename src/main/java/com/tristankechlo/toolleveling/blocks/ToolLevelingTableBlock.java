package com.tristankechlo.toolleveling.blocks;

import com.tristankechlo.toolleveling.blockentity.ToolLevelingTableBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fmllegacy.network.NetworkHooks;

public class ToolLevelingTableBlock extends BaseEntityBlock {

	private static final VoxelShape SHAPE = Shapes.or(box(2, 0, 2, 14, 3, 14), box(3, 3, 3, 13, 6, 13),
			box(4, 6, 4, 12, 11, 12), box(3, 11, 3, 13, 14, 13));

	public ToolLevelingTableBlock() {
		super(Block.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(4.5f, 1000.0f)
				.sound(SoundType.METAL).harvestLevel(2).harvestTool(ToolType.PICKAXE).requiresCorrectToolForDrops()
				.noOcclusion());
		this.registerDefaultState(this.defaultBlockState());
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
		return SHAPE;
	}

	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state) {
		return PushReaction.IGNORE;
	}

	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
			BlockHitResult hit) {
		if (level.isClientSide) {
			return InteractionResult.SUCCESS;
		} else {
			BlockEntity blockentity = level.getBlockEntity(pos);
			if (blockentity instanceof ToolLevelingTableBlockEntity) {
				NetworkHooks.openGui((ServerPlayer) player, (ToolLevelingTableBlockEntity) blockentity, buf -> {
					buf.writeBlockPos(pos);
				});
			}
			return InteractionResult.CONSUME;
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
		if (!state.is(newState.getBlock())) {
			BlockEntity blockentity = level.getBlockEntity(pos);
			if (blockentity instanceof ToolLevelingTableBlockEntity) {
				Containers.dropContents(level, pos, (ToolLevelingTableBlockEntity) blockentity);
				level.updateNeighbourForOutputSignal(pos, this);
			}
			super.onRemove(state, level, pos, newState, isMoving);
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return new ToolLevelingTableBlockEntity(pos, state);
	}

}
