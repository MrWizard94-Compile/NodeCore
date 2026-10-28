package com.mrwizard94.nodecore.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ArcaneOreBlock extends Block {

    public ArcaneOreBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_BLACK)
                .strength(4.0F, 8.0F)
                .sound(SoundType.AMETHYST_CLUSTER)
                .lightLevel(state -> 6)
                .requiresCorrectToolForDrops());
    }
}