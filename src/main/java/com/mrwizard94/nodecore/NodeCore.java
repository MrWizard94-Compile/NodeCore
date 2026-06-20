package com.mrwizard94.nodecore;

import com.mrwizard94.nodecore.command.NodeCoreCommands;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.event.ExtractionAlertHandler;
import com.mrwizard94.nodecore.event.LushGrowthHandler;
import com.mrwizard94.nodecore.event.NodeLodEventHandler;
import com.mrwizard94.nodecore.event.SurfaceSterilityHandler;
import com.mrwizard94.nodecore.worldgen.NodeLodBridge;
import com.mrwizard94.nodecore.worldgen.NodeLodComms;
import com.mrwizard94.nodecore.registry.ModBlocks;
import com.mrwizard94.nodecore.registry.ModCreativeTabs;
import com.mrwizard94.nodecore.registry.ModItems;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(NodeCore.MOD_ID)
public class NodeCore {
    public static final String MOD_ID = "nodecore";
    public static final Logger LOGGER = LogUtils.getLogger();

    public NodeCore() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NodeCoreConfig.COMMON_SPEC);

        modEventBus.addListener(NodeLodComms::processImc);

        MinecraftForge.EVENT_BUS.register(new SurfaceSterilityHandler());
        MinecraftForge.EVENT_BUS.register(new LushGrowthHandler());
        MinecraftForge.EVENT_BUS.register(new ExtractionAlertHandler());
        MinecraftForge.EVENT_BUS.register(new NodeLodEventHandler());
        MinecraftForge.EVENT_BUS.register(NodeLodComms.class);

        if (NodeLodBridge.isLodPresent()) {
            LOGGER.info("Large Ore Deposits detected — node registration bridge active.");
        } else {
            LOGGER.info("Large Ore Deposits not present — node bridge available via IMC and /nodecore link.");
        }
        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                NodeCoreCommands.register(event.getDispatcher()));

        LOGGER.info("Node Core initialized — Base Wars node systems online.");
    }
}