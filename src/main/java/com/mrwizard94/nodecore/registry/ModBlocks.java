package com.mrwizard94.nodecore.registry;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.block.NodeMarkerBlock;
import com.mrwizard94.nodecore.block.extractor.OreExtractorCoreBlock;
import com.mrwizard94.nodecore.block.extractor.OreExtractorFrameBlock;
import com.mrwizard94.nodecore.block.fluid.LiquidFireBlock;
import com.mrwizard94.nodecore.block.node.OreNodeBlock;

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

    public static final RegistryObject<Block> ORE_NODE = BLOCKS.register("ore_node",
            () -> new OreNodeBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .strength(-1.0F, 3600000.0F)
                    .sound(SoundType.AMETHYST)
                    .lightLevel(state -> 10)
                    .noOcclusion()
                    .noLootTable()));

    public static final RegistryObject<LiquidFireBlock> LIQUID_FIRE = BLOCKS.register("liquid_fire",
            () -> new LiquidFireBlock(ModFluids.LIQUID_FIRE_SOURCE, BlockBehaviour.Properties.of()
                    .mapColor(MapColor.COLOR_LIGHT_BLUE)
                    .replaceable()
                    .noCollission()
                    .strength(100.0F)
                    .lightLevel(state -> 11)
                    .noLootTable()));

    public static final RegistryObject<OreExtractorCoreBlock> ORE_EXTRACTOR_CORE = BLOCKS.register("ore_extractor_core",
            OreExtractorCoreBlock::new);

    public static final RegistryObject<OreExtractorFrameBlock> ORE_EXTRACTOR_FRAME = BLOCKS.register("ore_extractor_frame",
            OreExtractorFrameBlock::new);

    private ModBlocks() {}
}