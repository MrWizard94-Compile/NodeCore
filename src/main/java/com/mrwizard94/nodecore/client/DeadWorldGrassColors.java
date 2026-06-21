package com.mrwizard94.nodecore.client;

import com.mrwizard94.nodecore.NodeCore;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

@Mod.EventBusSubscriber(modid = NodeCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class DeadWorldGrassColors {
    private DeadWorldGrassColors() {
    }

    @SubscribeEvent
    public static void registerBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register(DeadWorldGrassColors::grassBlockColor, Blocks.GRASS_BLOCK);
        event.register(DeadWorldGrassColors::foliageBlockColor,
                Blocks.GRASS,
                Blocks.TALL_GRASS,
                Blocks.FERN,
                Blocks.LARGE_FERN);
    }

    private static int grassBlockColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (tintIndex != 0) {
            return -1;
        }
        return resolveGrassColor(level, pos);
    }

    private static int foliageBlockColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        return resolveFoliageColor(level, pos);
    }

    private static int resolveGrassColor(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        if (level == null || pos == null) {
            return DeadWorldTintRules.deadTanColor();
        }
        if (DeadWorldTintRules.shouldApplyDeadTint(level, pos)) {
            return DeadWorldTintRules.deadTanColor();
        }
        return BiomeColors.getAverageGrassColor(level, pos);
    }

    private static int resolveFoliageColor(@Nullable BlockAndTintGetter level, @Nullable BlockPos pos) {
        if (level == null || pos == null) {
            return DeadWorldTintRules.deadTanColor();
        }
        if (DeadWorldTintRules.shouldApplyDeadTint(level, pos)) {
            return DeadWorldTintRules.deadTanColor();
        }
        return BiomeColors.getAverageFoliageColor(level, pos);
    }
}