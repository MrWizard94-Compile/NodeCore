package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.worldgen.OreNodePlacer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NodeCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class OreNodeChunkHandler {
    private OreNodeChunkHandler() {}

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!NodeCoreConfig.ORE_NODE_WORLDGEN_ENABLED.get()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        OreNodePlacer.generateForChunk(level, event.getChunk().getPos());
        if (NodeCore.LOGGER.isDebugEnabled()) {
            NodeCore.LOGGER.debug("Ore-node worldgen pass complete for chunk {}", event.getChunk().getPos());
        }
    }
}