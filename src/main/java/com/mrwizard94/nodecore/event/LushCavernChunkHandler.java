package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.worldgen.LushCavernPlacer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NodeCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LushCavernChunkHandler {
    private LushCavernChunkHandler() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!NodeCoreConfig.LUSH_CAVERN_WORLDGEN_ENABLED.get()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        LushCavernPlacer.generateForChunk(level, event.getChunk().getPos());
        if (NodeCore.LOGGER.isDebugEnabled()) {
            NodeCore.LOGGER.debug("Lush cavern worldgen pass complete for chunk {}", event.getChunk().getPos());
        }
    }
}