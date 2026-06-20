package com.mrwizard94.nodecore.registry;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.block.NodeMarkerBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, NodeCore.MOD_ID);

    public static final RegistryObject<Block> NODE_MARKER = BLOCKS.register("node_marker",
            () -> new NodeMarkerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_PURPLE)
                    .strength(1.5f)
                    .sound(SoundType.AMETHYST)
                    .lightLevel(state -> 6)
                    .noOcclusion()));

    private ModBlocks() {}
}