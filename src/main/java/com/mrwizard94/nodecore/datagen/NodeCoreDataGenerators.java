package com.mrwizard94.nodecore.datagen;

import com.mrwizard94.nodecore.NodeCore;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NodeCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class NodeCoreDataGenerators {
    private NodeCoreDataGenerators() {}

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        generator.addProvider(event.includeServer(), new NodeSpacingWorldgenProvider(generator.getPackOutput()));
    }
}