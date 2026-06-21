package com.mrwizard94.nodecore.block.node;

import com.mrwizard94.nodecore.event.OreNodeHandler;
import com.mrwizard94.nodecore.node.NodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class OreNodeBlock extends Block {
    public static final EnumProperty<NodeType> NODE_TYPE =
            EnumProperty.create("node_type", NodeType.class);
    public static final IntegerProperty TIER = IntegerProperty.create("tier", 1, 3);

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 14, 14);

    public OreNodeBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(NODE_TYPE, NodeType.ORE_IRON)
                .setValue(TIER, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NODE_TYPE, TIER);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Deprecated // overriding deprecated vanilla hook (Forge-sanctioned); super-call required for vanilla behavior
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide() && !oldState.is(this)) {
            OreNodeHandler.onOreNodePlaced(level, pos, state);
        }
    }

    @Deprecated // overriding deprecated vanilla hook (Forge-sanctioned); super-call required for vanilla behavior
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            OreNodeHandler.onOreNodeRemoved(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}