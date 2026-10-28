package com.mrwizard94.nodecore.blockentity;

import com.mrwizard94.nodecore.block.extractor.ExtractorMultiblock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class OreExtractorFrameBlockEntity extends BlockEntity {
    public OreExtractorFrameBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ORE_EXTRACTOR_FRAME.get(), pos, state);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        if (level == null) {
            return super.getCapability(capability, side);
        }

        BlockEntity linked = ExtractorMultiblock.findCore(level, worldPosition)
                .map(level::getBlockEntity)
                .orElse(null);
        if (linked instanceof OreExtractorCoreBlockEntity core) {
            return core.getCapability(capability, side);
        }
        return super.getCapability(capability, side);
    }
}