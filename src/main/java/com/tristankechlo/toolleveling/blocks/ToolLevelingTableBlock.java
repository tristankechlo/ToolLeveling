package com.tristankechlo.toolleveling.blocks;

import com.tristankechlo.toolleveling.tileentity.ToolLevelingTableTileEntity;
import net.minecraft.block.*;
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

    private static final VoxelShape SHAPE = VoxelShapes.or(
            box(1, 0, 1, 15, 1, 15), box(1.5, 1, 1.5, 14.5, 2, 14.5),
            box(3, 2, 3, 13, 9, 13), box(1.5, 9, 1.5, 14.5, 13, 14.5)
    );

    public ToolLevelingTableBlock() {
        super(Block.Properties.of(Material.METAL, MaterialColor.COLOR_GRAY).strength(4.5f, 1000.0f)
                .sound(SoundType.METAL).harvestLevel(2).harvestTool(ToolType.PICKAXE).noOcclusion()
                .requiresCorrectToolForDrops());
        this.registerDefaultState(this.defaultBlockState());
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPE;
    }

    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.IGNORE;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        INamedContainerProvider namedContainerProvider = this.getMenuProvider(state, world, pos);
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
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.getBlock() != newState.getBlock()) {
            TileEntity tileentity = world.getBlockEntity(pos);
            if (tileentity instanceof ToolLevelingTableTileEntity) {
                ToolLevelingTableTileEntity table = (ToolLevelingTableTileEntity) tileentity;
                table.dropAllContents(world, pos);
            }
            super.onRemove(state, world, pos, newState, isMoving);
        }
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return newBlockEntity(world);
    }

    @Override
    public TileEntity newBlockEntity(IBlockReader worldIn) {
        return new ToolLevelingTableTileEntity();
    }

}
