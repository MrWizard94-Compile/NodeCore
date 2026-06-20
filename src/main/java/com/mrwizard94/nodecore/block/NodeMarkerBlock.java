package com.mrwizard94.nodecore.block;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.event.NodeMarkerHandler;
import com.mrwizard94.nodecore.node.NodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class NodeMarkerBlock extends Block {
    public static final EnumProperty<NodeType> NODE_TYPE = EnumProperty.create("node_type", NodeType.class);

    private static final VoxelShape SHAPE = Block.box(4, 0, 4, 12, 8, 12);

    public NodeMarkerBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(NODE_TYPE, NodeType.ORE_IRON));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NODE_TYPE);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(NODE_TYPE, resolveDefaultType());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
                                 BlockHitResult hit) {
        if (!player.isShiftKeyDown()) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        NodeType next = cycleNext(state.getValue(NODE_TYPE));
        level.setBlock(pos, state.setValue(NODE_TYPE, next), Block.UPDATE_ALL);
        player.displayClientMessage(
                net.minecraft.network.chat.Component.translatable("nodecore.marker.cycled_type", next.getDisplayName()),
                true);
        return InteractionResult.SUCCESS;
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!oldState.is(state.getBlock())) {
            NodeMarkerHandler.onMarkerPlaced(level, pos);
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            NodeMarkerHandler.onMarkerRemoved(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private static NodeType cycleNext(NodeType current) {
        NodeType[] values = NodeType.values();
        return values[(current.ordinal() + 1) % values.length];
    }

    private static NodeType resolveDefaultType() {
        try {
            return NodeType.byId(NodeCoreConfig.MARKER_DEFAULT_NODE_TYPE.get());
        } catch (IllegalArgumentException ex) {
            return NodeType.ORE_IRON;
        }
    }
}