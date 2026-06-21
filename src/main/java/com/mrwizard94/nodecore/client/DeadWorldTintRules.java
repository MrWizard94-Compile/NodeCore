package com.mrwizard94.nodecore.client;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

/**
 * Client-side rules for Base Wars dead-world grass tinting (spec §3).
 * Surface overworld flora renders dead-tan; lush underground caverns keep vanilla greens.
 */
public final class DeadWorldTintRules {
    /** Uniform dead-tan grass/foliage for the surface wasteland (spec §3). */
    public static final int DEAD_TAN_RGB = 0xB8A88A;

    private DeadWorldTintRules() {
    }

    public static boolean shouldApplyDeadTint(BlockAndTintGetter level, BlockPos pos) {
        if (!NodeCoreConfig.DEAD_WORLD_ENABLED.get()) {
            return false;
        }
        if (!isOverworld(level)) {
            return false;
        }
        if (LushCavernTintQueries.isInLushCavern(level, pos)) {
            return false;
        }
        return isSurfacePosition(level, pos);
    }

    public static int deadTanColor() {
        return DEAD_TAN_RGB;
    }

    private static boolean isOverworld(BlockAndTintGetter level) {
        if (level instanceof Level world) {
            return world.dimension() == Level.OVERWORLD;
        }
        return false;
    }

    /**
     * Surface = overworld Y &gt;= 0, or blocks exposed to open sky (ravines).
     * Underground lush caverns (child 8, below Y 0) are excluded via {@link LushCavernTintQueries}.
     */
    private static boolean isSurfacePosition(BlockAndTintGetter level, BlockPos pos) {
        if (pos.getY() >= 0) {
            return true;
        }
        if (level instanceof LevelReader reader) {
            return reader.canSeeSky(pos);
        }
        return false;
    }
}