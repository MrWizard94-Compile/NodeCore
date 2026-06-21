package com.mrwizard94.nodecore.registry;

import com.mojang.serialization.Codec;
import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.worldgen.DeadWorldBiomeModifier;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModBiomeModifiers {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, NodeCore.MOD_ID);

    public static final RegistryObject<Codec<? extends BiomeModifier>> DEAD_WORLD_STRIP_CODEC =
            SERIALIZERS.register("dead_world_strip", () -> DeadWorldBiomeModifier.CODEC);

    private ModBiomeModifiers() {}
}