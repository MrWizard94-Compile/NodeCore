package com.mrwizard94.nodecore.integration.dynamictrees;

import com.mrwizard94.nodecore.NodeCore;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Optional Dynamic Trees integration — reflection only, no compile-time dependency.
 */
public final class DynamicTreesBridge {
    private static final List<String> SPECIES_IDS = List.of(
            "dynamictrees:oak",
            "dynamictrees:birch",
            "dynamictrees:spruce",
            "dynamictrees:jungle"
    );

    private static volatile boolean probed;
    private static volatile Method growMethod;
    private static volatile Class<?> treeRegistryClass;

    private DynamicTreesBridge() {
    }

    public static boolean tryPlantTree(ServerLevel level, BlockPos pos, RandomSource random) {
        if (!DynamicTreesModProbe.isLoaded()) {
            return false;
        }
        if (tryGrowViaApi(level, pos, random)) {
            return true;
        }
        return placeDynamicSapling(level, pos, random);
    }

    private static boolean tryGrowViaApi(ServerLevel level, BlockPos pos, RandomSource random) {
        try {
            Class<?> registry = resolveTreeRegistryClass();
            if (registry == null) {
                return false;
            }
            Method getSpecies = registry.getMethod("getSpecies", ResourceLocation.class);
            String speciesId = SPECIES_IDS.get(random.nextInt(SPECIES_IDS.size()));
            ResourceLocation speciesKey = ResourceLocation.tryParse(speciesId);
            if (speciesKey == null) {
                return false;
            }
            Object species = getSpecies.invoke(null, speciesKey);
            if (species == null) {
                return false;
            }
            Method grow = resolveGrowMethod(species.getClass());
            if (grow == null) {
                return false;
            }
            Object result = grow.invoke(species, level, pos, level.getBiome(pos), random, false);
            return result instanceof Boolean bool && bool;
        } catch (ReflectiveOperationException error) {
            NodeCore.LOGGER.debug("Dynamic Trees API grow failed at {}: {}", pos, error.toString());
            return false;
        }
    }

    private static boolean placeDynamicSapling(ServerLevel level, BlockPos pos, RandomSource random) {
        for (int attempt = 0; attempt < SPECIES_IDS.size(); attempt++) {
            String speciesId = SPECIES_IDS.get(random.nextInt(SPECIES_IDS.size()));
            ResourceLocation saplingId = new ResourceLocation(
                    "dynamictrees",
                    speciesId.substring(speciesId.indexOf(':') + 1) + "_sapling"
            );
            Block block = ForgeRegistries.BLOCKS.getValue(saplingId);
            if (block == null) {
                continue;
            }
            BlockState state = block.defaultBlockState();
            if (!state.canSurvive(level, pos)) {
                continue;
            }
            level.setBlock(pos, state, Block.UPDATE_CLIENTS);
            return true;
        }
        return false;
    }

    private static Class<?> resolveTreeRegistryClass() {
        if (treeRegistryClass != null) {
            return treeRegistryClass;
        }
        try {
            treeRegistryClass = Class.forName("com.ferreusveritas.dynamictrees.tree.species.Species");
            return treeRegistryClass;
        } catch (ClassNotFoundException ignored) {
            try {
                treeRegistryClass = Class.forName("com.ferreusveritas.dynamictrees.api.registry.TreeRegistry");
                return treeRegistryClass;
            } catch (ClassNotFoundException ignoredAgain) {
                return null;
            }
        }
    }

    private static Method resolveGrowMethod(Class<?> type) {
        if (probed) {
            return growMethod;
        }
        probed = true;
        for (String name : List.of("generateTree", "plantSapling", "growTree")) {
            for (Method method : type.getMethods()) {
                if (!method.getName().equals(name)) {
                    continue;
                }
                Class<?>[] params = method.getParameterTypes();
                if (params.length >= 2 && ServerLevel.class.isAssignableFrom(params[0])) {
                    growMethod = method;
                    growMethod.setAccessible(true);
                    return growMethod;
                }
            }
        }
        return null;
    }
}