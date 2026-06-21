package com.mrwizard94.nodecore.blockentity;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.registry.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, NodeCore.MOD_ID);

    public static final RegistryObject<BlockEntityType<OreExtractorCoreBlockEntity>> ORE_EXTRACTOR_CORE =
            BLOCK_ENTITIES.register("ore_extractor_core",
                    () -> BlockEntityType.Builder.of(
                            OreExtractorCoreBlockEntity::new,
                            ModBlocks.ORE_EXTRACTOR_CORE.get()).build(null));

    private ModBlockEntities() {}
}