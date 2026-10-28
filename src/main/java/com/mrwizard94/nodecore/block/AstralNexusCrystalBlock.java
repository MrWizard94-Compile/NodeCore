package com.mrwizard94.nodecore.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class AstralNexusCrystalBlock extends Block {

    public AstralNexusCrystalBlock() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(3.0F, 6.0F)
                .sound(SoundType.AMETHYST)
                .lightLevel(state -> 8)
                .requiresCorrectToolForDrops()
                .noOcclusion());
    }
}