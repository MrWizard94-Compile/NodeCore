package com.mrwizard94.nodecore.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class NodeCoreConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec.BooleanValue SURFACE_STERILITY_ENABLED;
    public static final ForgeConfigSpec.BooleanValue LUSH_GROWTH_ENABLED;
    public static final ForgeConfigSpec.IntValue LUSH_GROWTH_INTERVAL_TICKS;
    public static final ForgeConfigSpec.BooleanValue EXTRACTION_ALERTS_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXTRACTION_ALERT_BLOCKS;
    public static final ForgeConfigSpec.IntValue DEFAULT_NODE_RADIUS;
    public static final ForgeConfigSpec.IntValue MIN_NODE_SPACING;
    public static final ForgeConfigSpec.IntValue MAX_NODE_SPACING;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("surface");
        SURFACE_STERILITY_ENABLED = builder
                .comment("Block planting seeds and saplings on blocks exposed to open sky.")
                .define("sterilityEnabled", true);
        builder.pop();

        builder.push("lush");
        LUSH_GROWTH_ENABLED = builder
                .comment("Accelerate crop and plant growth inside lush hydro-nodes.")
                .define("growthEnabled", true);
        LUSH_GROWTH_INTERVAL_TICKS = builder
                .comment("How often (in ticks) lush nodes attempt accelerated growth.")
                .defineInRange("growthIntervalTicks", 40, 5, 600);
        builder.pop();

        builder.push("extraction");
        EXTRACTION_ALERTS_ENABLED = builder
                .comment("Broadcast alerts when configured drill blocks are placed inside ore nodes.")
                .define("alertsEnabled", true);
        EXTRACTION_ALERT_BLOCKS = builder
                .comment("Block ids that trigger extraction alerts (e.g. create:mechanical_drill).")
                .defineList("alertBlocks", List.of("create:mechanical_drill"), o -> o instanceof String);
        builder.pop();

        builder.push("nodes");
        DEFAULT_NODE_RADIUS = builder
                .comment("Default radius (blocks) for newly registered nodes.")
                .defineInRange("defaultRadius", 48, 8, 256);
        MIN_NODE_SPACING = builder
                .comment("Minimum spacing hint for worldgen / manual placement (blocks).")
                .defineInRange("minSpacing", 2500, 500, 20000);
        MAX_NODE_SPACING = builder
                .comment("Maximum spacing hint for worldgen / manual placement (blocks).")
                .defineInRange("maxSpacing", 4000, 1000, 30000);
        builder.pop();

        COMMON_SPEC = builder.build();
    }

    private NodeCoreConfig() {}
}