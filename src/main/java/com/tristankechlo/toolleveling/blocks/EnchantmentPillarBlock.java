package com.tristankechlo.toolleveling.blocks;

import com.tristankechlo.toolleveling.tileentity.EnchantmentPillarTileEntity;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.items.ItemStackHandler;

public class EnchantmentPillarBlock extends Block {

	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
	public static final BooleanProperty ACTIVE = BooleanProperty.create("activated");

	private static final VoxelShape LOWER_SHAPE = VoxelShapes.or(makeCuboidShape(1, 0, 1, 15, 4, 15), makeCuboidShape(3, 4, 3, 13, 8, 13), makeCuboidShape(4, 8, 4, 12, 16, 12));
	private static final VoxelShape UPPER_SHAPE = VoxelShapes.or(makeCuboidShape(3, 0, 3, 13, 4, 13), makeCuboidShape(7, 0.5, 1.5, 9, 8.5, 4.5), makeCuboidShape(11.5, 0.5, 7, 14.5, 8.5, 9), makeCuboidShape(7, 0.5, 11.5, 9, 8.5, 14.5), makeCuboidShape(1.5, 0.5, 7, 4.5, 8.5, 9));
	private static final VoxelShape UPPER_SHAPE_ACTIVE = VoxelShapes.or(makeCuboidShape(3, 0, 3, 13, 4, 13), makeCuboidShape(7, 0, 0, 9, 10, 3));

	
	public EnchantmentPillarBlock() {
		super(Block.Properties.create(Material.IRON, MaterialColor.GRAY)
				.hardnessAndResistance(4.5f, 1000.0f)
				.sound(SoundType.METAL)
				.harvestLevel(2)
				.harvestTool(ToolType.PICKAXE)
				.notSolid().setRequiresTool());
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(HALF, DoubleBlockHalf.LOWER).with(ACTIVE, false));
	}

	@Override
	public int getHarvestLevel(BlockState state) {
		return 2;
	}

	@Override
	public ToolType getHarvestTool(BlockState state) {
		return ToolType.PICKAXE;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		if (state.get(HALF) == DoubleBlockHalf.UPPER) {
			if (state.get(ACTIVE)) {
				return VoxelShapes.or(LOWER_SHAPE.withOffset(0, -1, 0), UPPER_SHAPE_ACTIVE);
			} else {
				return VoxelShapes.or(LOWER_SHAPE.withOffset(0, -1, 0), UPPER_SHAPE);
			}
		} else {
			if (state.get(ACTIVE)) {
				return VoxelShapes.or(LOWER_SHAPE, UPPER_SHAPE_ACTIVE.withOffset(0, 1, 0));
			} else {
				return VoxelShapes.or(LOWER_SHAPE, UPPER_SHAPE.withOffset(0, 1, 0));
			}
		}
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
		if (!world.isRemote && handIn == Hand.MAIN_HAND) {
			if (state.get(HALF) == DoubleBlockHalf.LOWER) {
				if (this.handleInventory(world, pos, player)) {
					boolean active = state.get(ACTIVE);
					world.setBlockState(pos, state.with(ACTIVE, !active), 35);
					world.setBlockState(pos.up(), world.getBlockState(pos.up()).with(ACTIVE, !active), 35);
				}
			} else if (state.get(HALF) == DoubleBlockHalf.UPPER) {
				if (this.handleInventory(world, pos.down(), player)) {
					boolean active = state.get(ACTIVE);
					world.setBlockState(pos, state.with(ACTIVE, !active), 35);
					world.setBlockState(pos.down(), world.getBlockState(pos.down()).with(ACTIVE, !active), 35);
				}
			}
		}
		return ActionResultType.func_233537_a_(world.isRemote);
	}

	/**
	 * get or remove items from the pillar
	 * @param world
	 * @param pos
	 * @param player
	 * @return
	 */
	private boolean handleInventory(World world, BlockPos pos, PlayerEntity player) {
		ItemStack stack = player.getHeldItemMainhand();
		TileEntity entity = world.getTileEntity(pos);
		if (entity instanceof EnchantmentPillarTileEntity) {
			EnchantmentPillarTileEntity pillarEntity = (EnchantmentPillarTileEntity) entity;
			ItemStackHandler pillarInv = pillarEntity.inventory;
			//add item to the pillar
			if (!stack.isEmpty() && pillarInv.getStackInSlot(0).isEmpty() && pillarInv.isItemValid(0, stack)) {
				pillarInv.setStackInSlot(0, stack.copy());
				stack.setCount(0);;
				pillarEntity.markDirty();
				return true;

			//remove item from pillar
			} else if (stack.isEmpty() && !pillarInv.getStackInSlot(0).isEmpty()) {
				player.inventory.addItemStackToInventory(pillarInv.getStackInSlot(0));
				pillarInv.setStackInSlot(0, ItemStack.EMPTY);
				pillarEntity.markDirty();
				return true;
			} else {
				return false;
			}
		}
		return false;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		if(state.get(HALF) == DoubleBlockHalf.LOWER) {
			return true;
		}
		return false;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new EnchantmentPillarTileEntity();
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,	BlockPos currentPos, BlockPos facingPos) {
		DoubleBlockHalf doubleblockhalf = stateIn.get(HALF);
		if (facing.getAxis() != Direction.Axis.Y || doubleblockhalf == DoubleBlockHalf.LOWER != (facing == Direction.UP) || facingState.isIn(this) && facingState.get(HALF) != doubleblockhalf) {
			return doubleblockhalf == DoubleBlockHalf.LOWER && facing == Direction.DOWN	&& !stateIn.isValidPosition(worldIn, currentPos) 
					? Blocks.AIR.getDefaultState()
					: super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
		} else {
			return Blocks.AIR.getDefaultState();
		}
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		BlockPos blockpos = context.getPos();
		return blockpos.getY() < 255 && context.getWorld().getBlockState(blockpos.up()).isReplaceable(context)
				? super.getStateForPlacement(context)
				: null;
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		worldIn.setBlockState(pos.up(), this.getDefaultState().with(HALF, DoubleBlockHalf.UPPER), 3);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
		if (state.get(HALF) != DoubleBlockHalf.UPPER) {
			return super.isValidPosition(state, worldIn, pos);
		} else {
			BlockState blockstate = worldIn.getBlockState(pos.down());
			if (state.getBlock() != this)
				return super.isValidPosition(state, worldIn, pos);
			return blockstate.isIn(this) && blockstate.get(HALF) == DoubleBlockHalf.LOWER;
		}
	}
	
	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
	      if (state.hasTileEntity() && (state.getBlock() != newState.getBlock() || !newState.hasTileEntity())) {
	    	  TileEntity tile = world.getTileEntity(pos);
	    	  if(tile instanceof EnchantmentPillarTileEntity) {
	    		  ItemStackHandler inventory = ((EnchantmentPillarTileEntity)tile).inventory;
	    		  ItemStack stack = inventory.getStackInSlot(0);
	    		  if(stack != ItemStack.EMPTY) {
		    		  InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack);
	    		  }
	    	  }
		      world.removeTileEntity(pos);
	      }
	}

	@Override
	public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
		if (!worldIn.isRemote) {
			if (player.isCreative()) {
				removeBottomHalf(worldIn, pos, state, player);
			} else {
				spawnDrops(state, worldIn, pos, (TileEntity) null, player, player.getHeldItemMainhand());
			}
		}
		super.onBlockHarvested(worldIn, pos, state, player);
	}

	protected static void removeBottomHalf(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		DoubleBlockHalf doubleblockhalf = state.get(HALF);
		if (doubleblockhalf == DoubleBlockHalf.UPPER) {
			BlockPos blockpos = pos.down();
			BlockState blockstate = world.getBlockState(blockpos);
			if (blockstate.getBlock() == state.getBlock() && blockstate.get(HALF) == DoubleBlockHalf.LOWER) {
				world.setBlockState(blockpos, Blocks.AIR.getDefaultState(), 35);
				world.playEvent(player, 2001, blockpos, Block.getStateId(blockstate));
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
		//block can't be mined when the upgrading started
		boolean upgradeStarted = false;
		if(upgradeStarted) {
			return -1F;
		}
		return super.getPlayerRelativeBlockHardness(state, player, worldIn, pos);
	}

	@Override
	public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, TileEntity te,	ItemStack stack) {
		super.harvestBlock(worldIn, player, pos, Blocks.AIR.getDefaultState(), te, stack);
	}

	@Override
	protected void fillStateContainer(Builder<Block, BlockState> builder) {
		builder.add(FACING, HALF, ACTIVE);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	@Deprecated
	@Override
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	@Override
	public OffsetType getOffsetType() {
		return AbstractBlock.OffsetType.NONE;
	}

	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public PushReaction getPushReaction(BlockState state) {
		return PushReaction.DESTROY;
	}
}
