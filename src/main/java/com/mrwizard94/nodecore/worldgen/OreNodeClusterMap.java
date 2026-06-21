package com.mrwizard94.nodecore.worldgen;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.node.NodeType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class OreNodeClusterMap {
    private static final List<NodeType> FALLBACK_ORE_TYPES = List.of(
            NodeType.ORE_IRON,
            NodeType.ORE_COPPER,
            NodeType.ORE_BRASS,
            NodeType.ORE_QUARTZ
    );

    private OreNodeClusterMap() {}

    public static NodeType resolveType(ServerLevel level, BlockPos pos) {
        Optional<NodeType> biomeType = typeForBiome(level, pos);
        if (biomeType.isPresent()) {
            return biomeType.get();
        }
        return clusterType(level, pos);
    }

    public static int rollTier(ServerLevel level, BlockPos pos) {
        int weight1 = NodeCoreConfig.ORE_NODE_TIER_1_WEIGHT.get();
        int weight2 = NodeCoreConfig.ORE_NODE_TIER_2_WEIGHT.get();
        int weight3 = NodeCoreConfig.ORE_NODE_TIER_3_WEIGHT.get();
        int total = weight1 + weight2 + weight3;
        if (total <= 0) {
            return 1;
        }

        long hash = mixSeed(level.getSeed() ^ 0x546965724CL, pos.getX(), pos.getZ());
        int roll = (int) Math.floorMod(hash, total);
        if (roll < weight1) {
            return 1;
        }
        if (roll < weight1 + weight2) {
            return 2;
        }
        return 3;
    }

    public static boolean shouldPlaceAtGrid(ServerLevel level, int gridX, int gridZ) {
        double frequency = NodeCoreConfig.ORE_NODE_FREQUENCY.get();
        if (frequency <= 0.0D) {
            return false;
        }
        if (frequency >= 1.0D) {
            return true;
        }

        long hash = mixSeed(level.getSeed() ^ 0x4E6F6465474EL, gridX, gridZ);
        double roll = (hash & 0xFFFFL) / 65535.0D;
        return roll < frequency;
    }

    private static NodeType clusterType(ServerLevel level, BlockPos pos) {
        int clusterSize = NodeCoreConfig.ORE_NODE_CLUSTER_SIZE.get();
        int clusterX = Math.floorDiv(pos.getX(), clusterSize);
        int clusterZ = Math.floorDiv(pos.getZ(), clusterSize);
        long hash = mixSeed(level.getSeed() ^ 0x436C7573744CL, clusterX, clusterZ);
        List<NodeType> oreTypes = configuredOreTypes();
        int index = (int) Math.floorMod(hash, oreTypes.size());
        return oreTypes.get(index);
    }

    private static Optional<NodeType> typeForBiome(ServerLevel level, BlockPos pos) {
        Holder<Biome> biome = level.getBiome(pos);
        ResourceKey<Biome> key = biome.unwrapKey().orElse(null);
        if (key == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(biomeMappings().get(key.location()));
    }

    private static List<NodeType> configuredOreTypes() {
        List<NodeType> types = new ArrayList<>();
        for (String entry : NodeCoreConfig.ORE_NODE_METAL_TYPES.get()) {
            parseType(entry).filter(NodeType::isOre).ifPresent(type -> {
                if (!types.contains(type)) {
                    types.add(type);
                }
            });
        }
        return types.isEmpty() ? FALLBACK_ORE_TYPES : List.copyOf(types);
    }

    private static Map<ResourceLocation, NodeType> biomeMappings() {
        Map<ResourceLocation, NodeType> mappings = new LinkedHashMap<>();
        for (String entry : NodeCoreConfig.ORE_NODE_BIOME_METAL_TYPES.get()) {
            int separator = entry.indexOf('=');
            if (separator <= 0 || separator >= entry.length() - 1) {
                continue;
            }
            ResourceLocation biomeId = ResourceLocation.tryParse(entry.substring(0, separator).trim());
            if (biomeId == null) {
                continue;
            }
            parseType(entry.substring(separator + 1).trim())
                    .filter(NodeType::isOre)
                    .ifPresent(type -> mappings.put(biomeId, type));
        }
        return mappings;
    }

    private static Optional<NodeType> parseType(String raw) {
        if (raw == null || raw.isBlank()) {
            return Optional.empty();
        }
        try {
            return Optional.of(NodeType.byId(raw.trim().toLowerCase(Locale.ROOT)));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private static long mixSeed(long seed, int x, int z) {
        long hash = seed;
        hash ^= (long) x * 0x9E3779B97F4A7C15L;
        hash ^= (long) z * 0xC2B2AE3D27D4EB4FL;
        hash ^= hash >>> 33;
        hash *= 0xff51afd7ed558ccdL;
        hash ^= hash >>> 33;
        hash *= 0xc4ceb9fe1a85ec53L;
        hash ^= hash >>> 33;
        return hash;
    }
}