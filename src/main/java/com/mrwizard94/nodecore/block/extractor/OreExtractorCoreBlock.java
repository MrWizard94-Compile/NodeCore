package com.mrwizard94.nodecore.block.extractor;

import com.mrwizard94.nodecore.blockentity.OreExtractorCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class OreExtractorCoreBlock extends BaseEntityBlock {
    public OreExtractorCoreBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_GRAY)
                .strength(3.5F, 6.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OreExtractorCoreBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
                                                                  BlockEntityType<T> type) {
        return level.isClientSide()
                ? null
                : createTickerHelper(type, com.mrwizard94.nodecore.blockentity.ModBlockEntities.ORE_EXTRACTOR_CORE.get(),
                OreExtractorCoreBlockEntity::serverTick);
    }

    @Deprecated // overriding deprecated vanilla hook (Forge-sanctioned); super-call required for vanilla behavior
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof OreExtractorCoreBlockEntity core) {
            core.tryClaimNode((ServerLevel) level);
        }
    }

    @Deprecated // overriding deprecated vanilla hook (Forge-sanctioned); super-call required for vanilla behavior
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof OreExtractorCoreBlockEntity core) {
            core.releaseClaim((ServerLevel) level);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof OreExtractorCoreBlockEntity core) {
            player.displayClientMessage(core.statusLine(), true);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.sidedSuccess(level.isClientSide());
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.Explosion explosion) {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof OreExtractorCoreBlockEntity core) {
            core.releaseClaim((ServerLevel) level);
        }
        super.onBlockExploded(state, level, pos, explosion);
    }
}