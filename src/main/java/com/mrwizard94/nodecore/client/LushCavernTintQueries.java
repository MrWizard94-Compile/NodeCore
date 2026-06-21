package com.mrwizard94.nodecore.client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;

/**
 * Client hook for lush-cavern volume membership — delegates to synced cavern registry.
 * Canonical server query: {@link com.mrwizard94.nodecore.node.NodeQueries#isInLushCavern}.
 */
public final class LushCavernTintQueries {
    private LushCavernTintQueries() {
    }

    public static boolean isInLushCavern(BlockAndTintGetter level, BlockPos pos) {
        return LushCavernClientCache.contains(pos);
    }
}