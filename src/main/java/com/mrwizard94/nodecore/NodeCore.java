package com.mrwizard94.nodecore;

import com.mrwizard94.nodecore.command.NodeCoreCommands;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.event.ExtractionAlertHandler;
import com.mrwizard94.nodecore.event.LushGrowthHandler;
import com.mrwizard94.nodecore.event.SurfaceSterilityHandler;
import com.mrwizard94.nodecore.integration.NodeInControlBridge;
import com.mrwizard94.nodecore.registry.ModArgumentTypes;
import com.mrwizard94.nodecore.registry.ModBiomeModifiers;
import com.mrwizard94.nodecore.blockentity.ModBlockEntities;
import com.mrwizard94.nodecore.registry.ModBlocks;
import com.mrwizard94.nodecore.registry.ModCreativeTabs;
import com.mrwizard94.nodecore.registry.ModFluidTypes;
import com.mrwizard94.nodecore.registry.ModFluids;
import com.mrwizard94.nodecore.registry.ModItems;
import com.mrwizard94.nodecore.network.NodeCoreNetwork;
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

        ModFluidTypes.FLUID_TYPES.register(modEventBus);
        ModFluids.FLUIDS.register(modEventBus);
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModCreativeTabs.CREATIVE_TABS.register(modEventBus);
        ModBiomeModifiers.SERIALIZERS.register(modEventBus);
        ModArgumentTypes.ARGUMENT_TYPES.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, NodeCoreConfig.COMMON_SPEC);

        NodeCoreNetwork.register();

        MinecraftForge.EVENT_BUS.register(new SurfaceSterilityHandler());
        MinecraftForge.EVENT_BUS.register(new LushGrowthHandler());
        MinecraftForge.EVENT_BUS.register(new ExtractionAlertHandler());

        if (NodeCoreConfig.ORE_NODE_WORLDGEN_ENABLED.getDefault()) {
            LOGGER.info("Ore-node worldgen enabled — clustered typed/tiered fixtures.");
        }

        if (NodeInControlBridge.isInControlPresent()) {
            LOGGER.info("In Control detected — node spawn export available via /nodecore export incontrol.");
        } else {
            LOGGER.info("In Control not present — spawn rule export still available for manual pack merge.");
        }

        if (NodeCoreConfig.DEAD_WORLD_ENABLED.getDefault()) {
            LOGGER.info("Dead-world biome strip enabled — new overworld chunks generate without surface vegetation.");
        }

        if (NodeCoreConfig.EXTRACTOR_ENABLED.getDefault()) {
            LOGGER.info("Ore extractor multiblocks enabled — Create rotational drive with fuel ladder and buffer backpressure.");
        }

        if (NodeCoreConfig.LUSH_CAVERN_WORLDGEN_ENABLED.getDefault()) {
            LOGGER.info("Lush cavern worldgen enabled — massive sub-zero farming strongholds with 3D volume membership.");
        }

        MinecraftForge.EVENT_BUS.addListener((RegisterCommandsEvent event) ->
                NodeCoreCommands.register(event.getDispatcher()));

        LOGGER.info("Node Core initialized — Base Wars node systems online.");
    }
}
