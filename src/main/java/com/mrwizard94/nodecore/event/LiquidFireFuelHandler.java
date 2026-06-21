package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.registry.ModItems;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NodeCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LiquidFireFuelHandler {
    private LiquidFireFuelHandler() {}

    @SubscribeEvent
    public static void onFuelBurnTime(FurnaceFuelBurnTimeEvent event) {
        if (event.getItemStack().is(ModItems.LIQUID_FIRE_BUCKET.get())) {
            event.setBurnTime(NodeCoreConfig.LIQUID_FIRE_FUEL_BURN_TIME.get());
        }
    }
}