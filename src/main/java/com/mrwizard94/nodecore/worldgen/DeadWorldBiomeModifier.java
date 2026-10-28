package com.mrwizard94.nodecore.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

public record DeadWorldBiomeModifier(HolderSet<Biome> biomes) implements BiomeModifier {
    public static final Codec<DeadWorldBiomeModifier> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Biome.LIST_CODEC.fieldOf("biomes").forGetter(DeadWorldBiomeModifier::biomes)
            ).apply(instance, DeadWorldBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        // REMOVE phase runs after every mod's ADD-phase feature injection, so
        // clearing here strips vanilla AND modded ores/vegetation comprehensively
        // (ADD phase would miss ores other mods add after us).
        if (phase != Phase.REMOVE || !NodeCoreConfig.DEAD_WORLD_ENABLED.get()) {
            return;
        }
        if (!biomes.contains(biome)) {
            return;
        }

        BiomeGenerationSettingsBuilder generation = builder.getGenerationSettings();
        if (NodeCoreConfig.DEAD_WORLD_STRIP_VEGETATION.get()) {
            clearStep(generation, GenerationStep.Decoration.VEGETAL_DECORATION);
            clearStep(generation, GenerationStep.Decoration.TOP_LAYER_MODIFICATION);
        }
        if (NodeCoreConfig.DEAD_WORLD_STRIP_SCATTER_ORES.get()) {
            clearStep(generation, GenerationStep.Decoration.UNDERGROUND_ORES);
            clearStep(generation, GenerationStep.Decoration.UNDERGROUND_DECORATION);
        }
    }

    private static void clearStep(BiomeGenerationSettingsBuilder generation, GenerationStep.Decoration step) {
        generation.getFeatures(step).clear();
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}