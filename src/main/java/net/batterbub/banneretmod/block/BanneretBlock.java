package net.batterbub.banneretmod.block;

import com.google.common.collect.Maps;
import com.mojang.serialization.MapCodec;
import net.batterbub.banneretmod.block.entity.BanneretBlockEntity;
import net.batterbub.banneretmod.config.BanneretConfig;
import net.batterbub.banneretmod.config.BanneretConfigHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public class BanneretBlock extends BaseEntityBlock {
    public static final VoxelShape SHAPE;
    public static final MapCodec<BanneretBlock> CODEC = simpleCodec(BanneretBlock::new);
    public static final DirectionProperty FACING;

    public BanneretBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos below = pos.below();
        return level.getBlockState(below).isSolid();
    }

    @Override
    public BlockState updateShape(BlockState state,
                                  Direction direction,
                                  BlockState neighborState,
                                  LevelAccessor level,
                                  BlockPos pos,
                                  BlockPos neighborPos) {

        // If the block below changed and is no longer solid → break the banneret
        if (direction == Direction.DOWN && !canSurvive(state, level, pos)) {
            level.destroyBlock(pos, false); // false = no drops
        }

        return state;
    }

    /* Block Entity Stuff */

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new BanneretBlockEntity(blockPos, blockState);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moveByPiston){
        if(state.getBlock() != newState.getBlock()) {
            if(level.getBlockEntity(pos) instanceof  BanneretBlockEntity banneretBlockEntity) {
                BlockPos belowPos = pos.below();
                BlockState belowState = level.getBlockState(belowPos);
                Block belowBlock = belowState.getBlock();

                banneretBlockEntity.unClaimAround(level, pos,
                        BanneretConfig.COMMON.baseClaimRadius.get() + BanneretConfigHandler.BLOCK_RADIUS_MAP.getOrDefault(belowBlock, 0));
            }
        }
    }

    static {
        FACING = BlockStateProperties.FACING;
        SHAPE = Block.box((double)4.0F, (double)0.0F, (double)4.0F, (double)12.0F, (double)16.0F, (double)12.0F);
    }
}
