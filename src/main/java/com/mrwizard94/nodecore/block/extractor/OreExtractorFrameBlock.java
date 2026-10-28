package com.mrwizard94.nodecore.block.extractor;

import com.mrwizard94.nodecore.blockentity.OreExtractorCoreBlockEntity;
import com.mrwizard94.nodecore.blockentity.OreExtractorFrameBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;

import javax.annotation.Nullable;

public class OreExtractorFrameBlock extends BaseEntityBlock {
    public OreExtractorFrameBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(3.5F, 6.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops());
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new OreExtractorFrameBlockEntity(pos, state);
    }

    @Deprecated // overriding deprecated vanilla hook (Forge-sanctioned); super-call required for vanilla behavior
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        notifyLinkedCore(level, pos);
    }

    @Deprecated // overriding deprecated vanilla hook (Forge-sanctioned); super-call required for vanilla behavior
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            notifyLinkedCore(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                               InteractionHand hand, BlockHitResult hit) {
        return ExtractorMultiblock.findCore(level, pos)
                .map(level::getBlockEntity)
                .filter(OreExtractorCoreBlockEntity.class::isInstance)
                .map(OreExtractorCoreBlockEntity.class::cast)
                .map(core -> {
                    if (!level.isClientSide()) {
                        player.displayClientMessage(core.statusLine(), true);
                    }
                    return InteractionResult.sidedSuccess(level.isClientSide());
                })
                .orElse(InteractionResult.PASS);
    }

    private static void notifyLinkedCore(Level level, BlockPos pos) {
        if (level.isClientSide()) {
            return;
        }
        ExtractorMultiblock.findCore(level, pos)
                .map(level::getBlockEntity)
                .filter(OreExtractorCoreBlockEntity.class::isInstance)
                .map(OreExtractorCoreBlockEntity.class::cast)
                .ifPresent(OreExtractorCoreBlockEntity::requestStructureRecheck);
    }
}