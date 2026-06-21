package com.mrwizard94.nodecore.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public final class NodeCoreConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec.BooleanValue DEAD_WORLD_ENABLED;
    public static final ForgeConfigSpec.BooleanValue DEAD_WORLD_STRIP_VEGETATION;
    public static final ForgeConfigSpec.BooleanValue DEAD_WORLD_STRIP_SCATTER_ORES;
    public static final ForgeConfigSpec.BooleanValue DEAD_WORLD_GROWTH_LUSH_ONLY;
    public static final ForgeConfigSpec.BooleanValue SURFACE_STERILITY_ENABLED;
    public static final ForgeConfigSpec.BooleanValue LUSH_GROWTH_ENABLED;
    public static final ForgeConfigSpec.IntValue LUSH_GROWTH_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue LUSH_GROWTH_SAMPLES_PER_PASS;
    public static final ForgeConfigSpec.IntValue LUSH_GROWTH_SAMPLE_RADIUS;
    public static final ForgeConfigSpec.IntValue LUSH_GROWTH_SCAN_RADIUS;
    public static final ForgeConfigSpec.IntValue LUSH_GROWTH_EXTRA_RANDOM_TICKS;
    public static final ForgeConfigSpec.IntValue LUSH_GROWTH_MIN_Y;
    public static final ForgeConfigSpec.IntValue LUSH_GROWTH_MAX_Y;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> LUSH_GROWTH_BLOCK_MULTIPLIERS;
    public static final ForgeConfigSpec.BooleanValue EXTRACTION_ALERTS_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXTRACTION_ALERT_BLOCKS;
    public static final ForgeConfigSpec.IntValue DEFAULT_NODE_RADIUS;
    public static final ForgeConfigSpec.IntValue MIN_NODE_SPACING;
    public static final ForgeConfigSpec.IntValue MAX_NODE_SPACING;
    public static final ForgeConfigSpec.ConfigValue<String> MARKER_DEFAULT_NODE_TYPE;
    public static final ForgeConfigSpec.BooleanValue ORE_NODE_WORLDGEN_ENABLED;
    public static final ForgeConfigSpec.DoubleValue ORE_NODE_FREQUENCY;
    public static final ForgeConfigSpec.IntValue ORE_NODE_CLUSTER_SIZE;
    public static final ForgeConfigSpec.IntValue ORE_NODE_MIN_Y;
    public static final ForgeConfigSpec.IntValue ORE_NODE_MAX_Y;
    public static final ForgeConfigSpec.IntValue ORE_NODE_TIER_1_WEIGHT;
    public static final ForgeConfigSpec.IntValue ORE_NODE_TIER_2_WEIGHT;
    public static final ForgeConfigSpec.IntValue ORE_NODE_TIER_3_WEIGHT;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ORE_NODE_METAL_TYPES;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ORE_NODE_BIOME_METAL_TYPES;
    public static final ForgeConfigSpec.BooleanValue SPAWN_EXPORT_ENABLED;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> SPAWN_MOBS_BY_TYPE;
    public static final ForgeConfigSpec.BooleanValue LIQUID_FIRE_COLLECTION_ENABLED;
    public static final ForgeConfigSpec.IntValue LIQUID_FIRE_COLLECTION_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue LIQUID_FIRE_COLLECTION_FILL_TICKS;
    public static final ForgeConfigSpec.ConfigValue<String> LIQUID_FIRE_TINT;
    public static final ForgeConfigSpec.IntValue LIQUID_FIRE_FUEL_BURN_TIME;
    public static final ForgeConfigSpec.IntValue LIQUID_FIRE_PARTICLE_CHANCE;
    public static final ForgeConfigSpec.BooleanValue EXTRACTOR_ENABLED;
    public static final ForgeConfigSpec.DoubleValue EXTRACTOR_BASE_PRODUCTION_PER_TICK;
    public static final ForgeConfigSpec.DoubleValue EXTRACTOR_TIER_1_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue EXTRACTOR_TIER_2_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue EXTRACTOR_TIER_3_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue EXTRACTOR_FUEL_BOOST_CHARCOAL;
    public static final ForgeConfigSpec.DoubleValue EXTRACTOR_FUEL_BOOST_COAL;
    public static final ForgeConfigSpec.DoubleValue EXTRACTOR_FUEL_BOOST_LAVA;
    public static final ForgeConfigSpec.DoubleValue EXTRACTOR_FUEL_BOOST_LIQUID_FIRE;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_FUEL_BURN_CHARCOAL;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_FUEL_BURN_COAL;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_FUEL_BURN_LAVA;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_FUEL_BURN_LIQUID_FIRE;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_LAVA_TANK_CAPACITY;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_LAVA_CONSUMPTION_MB;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_BUFFER_SLOTS;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_BUFFER_STACK_LIMIT;
    public static final ForgeConfigSpec.DoubleValue EXTRACTOR_RAW_ORE_BLOCK_CHANCE;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_DECAY_INTERVAL_TICKS;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_DECAY_AMOUNT;
    public static final ForgeConfigSpec.DoubleValue EXTRACTOR_MIN_ROTATION_SPEED;
    public static final ForgeConfigSpec.IntValue EXTRACTOR_STRUCTURE_CHECK_INTERVAL;
    public static final ForgeConfigSpec.BooleanValue NO_BUILD_AURA_ENABLED;
    public static final ForgeConfigSpec.IntValue NO_BUILD_AURA_RADIUS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> NO_BUILD_AURA_WHITELIST;
    public static final ForgeConfigSpec.BooleanValue LUSH_CAVERN_WORLDGEN_ENABLED;
    public static final ForgeConfigSpec.DoubleValue LUSH_CAVERN_FREQUENCY;
    public static final ForgeConfigSpec.IntValue LUSH_CAVERN_GRID_SPACING;
    public static final ForgeConfigSpec.IntValue LUSH_CAVERN_MIN_Y;
    public static final ForgeConfigSpec.IntValue LUSH_CAVERN_MAX_Y;
    public static final ForgeConfigSpec.IntValue LUSH_CAVERN_MIN_HORIZONTAL_RADIUS;
    public static final ForgeConfigSpec.IntValue LUSH_CAVERN_MAX_HORIZONTAL_RADIUS;
    public static final ForgeConfigSpec.IntValue LUSH_CAVERN_MIN_VERTICAL_RADIUS;
    public static final ForgeConfigSpec.IntValue LUSH_CAVERN_MAX_VERTICAL_RADIUS;
    public static final ForgeConfigSpec.DoubleValue LUSH_CAVERN_FLORA_DENSITY;
    public static final ForgeConfigSpec.DoubleValue LUSH_CAVERN_TREE_DENSITY;
    public static final ForgeConfigSpec.DoubleValue LUSH_CAVERN_MOSS_DENSITY;
    public static final ForgeConfigSpec.BooleanValue LUSH_CAVERN_REGISTER_TERRITORY;
    public static final ForgeConfigSpec.IntValue LUSH_CAVERN_TERRITORY_PADDING;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("deadWorld");
        DEAD_WORLD_ENABLED = builder
                .comment("Master switch for Base Wars dead-world rules (worldgen strip, lush-only growth).")
                .define("enabled", true);
        DEAD_WORLD_STRIP_VEGETATION = builder
                .comment("Remove trees, grass, flowers, and cave vegetation from overworld biome generation.")
                .define("stripVegetation", true);
        DEAD_WORLD_STRIP_SCATTER_ORES = builder
                .comment("Remove scattered underground ore features from overworld biome generation.")
                .define("stripScatterOres", true);
        DEAD_WORLD_GROWTH_LUSH_ONLY = builder
                .comment("Allow crop and plant growth only inside registered lush_hydro nodes.")
                .define("growthLushOnly", true);
        builder.pop();

        builder.push("surface");
        SURFACE_STERILITY_ENABLED = builder
                .comment("Block planting seeds and saplings on blocks exposed to open sky.")
                .define("sterilityEnabled", true);
        builder.pop();

        builder.push("lush");
        LUSH_GROWTH_ENABLED = builder
                .comment("Accelerate crop and plant growth inside lush cavern volumes (3D SavedData membership).")
                .define("growthEnabled", true);
        LUSH_GROWTH_INTERVAL_TICKS = builder
                .comment("How often (in ticks) the growth booster samples cavern volumes.")
                .defineInRange("growthIntervalTicks", 40, 5, 600);
        LUSH_GROWTH_SAMPLES_PER_PASS = builder
                .comment("Random 3D sample anchors attempted per growth interval (not per game tick).")
                .defineInRange("growthSamplesPerPass", 6, 1, 64);
        LUSH_GROWTH_SAMPLE_RADIUS = builder
                .comment("Horizontal radius around online players for random underground sample anchors.")
                .defineInRange("growthSampleRadius", 48, 8, 256);
        LUSH_GROWTH_SCAN_RADIUS = builder
                .comment("Cube half-extent (blocks) scanned around each lush sample anchor.")
                .defineInRange("growthScanRadius", 4, 1, 16);
        LUSH_GROWTH_EXTRA_RANDOM_TICKS = builder
                .comment("Extra randomTick invocations applied to each growable block per boost hit.")
                .defineInRange("growthExtraRandomTicks", 3, 1, 32);
        LUSH_GROWTH_MIN_Y = builder
                .comment("Minimum Y for underground growth sampling (defaults align with lushCavern.minY).")
                .defineInRange("growthMinY", -56, -64, 320);
        LUSH_GROWTH_MAX_Y = builder
                .comment("Maximum Y for underground growth sampling (defaults align with lushCavern.maxY).")
                .defineInRange("growthMaxY", -8, -64, 320);
        LUSH_GROWTH_BLOCK_MULTIPLIERS = builder
                .comment("Optional per-block extra-tick overrides (block_id=tick_count). Omitted blocks use growthExtraRandomTicks.")
                .defineList("growthBlockMultipliers", List.of(
                        "minecraft:wheat=4",
                        "minecraft:carrots=4",
                        "minecraft:potatoes=4",
                        "minecraft:beetroots=4",
                        "minecraft:oak_sapling=2",
                        "minecraft:birch_sapling=2",
                        "minecraft:spruce_sapling=2",
                        "minecraft:jungle_sapling=2"
                ), o -> o instanceof String);
        builder.pop();

        builder.push("extraction");
        EXTRACTION_ALERTS_ENABLED = builder
                .comment("Broadcast alerts when configured drill blocks are placed inside ore nodes.")
                .define("alertsEnabled", true);
        EXTRACTION_ALERT_BLOCKS = builder
                .comment("Block ids that trigger extraction alerts (e.g. create:mechanical_drill).")
                .defineList("alertBlocks", List.of(
                        "create:mechanical_drill",
                        "nodecore:ore_extractor_core"
                ), o -> o instanceof String);
        builder.pop();

        builder.push("extractor");
        EXTRACTOR_ENABLED = builder
                .comment("Master switch for ore-node extractor multiblocks.")
                .define("enabled", true);
        EXTRACTOR_BASE_PRODUCTION_PER_TICK = builder
                .comment("Base ore output rate per tick before tier and fuel multipliers.")
                .defineInRange("baseProductionPerTick", 0.05D, 0.001D, 10.0D);
        EXTRACTOR_TIER_1_MULTIPLIER = builder
                .comment("Production multiplier for tier-1 nodes.")
                .defineInRange("tier1Multiplier", 1.0D, 0.1D, 20.0D);
        EXTRACTOR_TIER_2_MULTIPLIER = builder
                .comment("Production multiplier for tier-2 nodes.")
                .defineInRange("tier2Multiplier", 1.75D, 0.1D, 20.0D);
        EXTRACTOR_TIER_3_MULTIPLIER = builder
                .comment("Production multiplier for tier-3 nodes.")
                .defineInRange("tier3Multiplier", 2.5D, 0.1D, 20.0D);
        EXTRACTOR_FUEL_BOOST_CHARCOAL = builder
                .comment("Production-speed boost while charcoal fuel is active.")
                .defineInRange("fuelBoostCharcoal", 1.15D, 1.0D, 10.0D);
        EXTRACTOR_FUEL_BOOST_COAL = builder
                .comment("Production-speed boost while coal fuel is active.")
                .defineInRange("fuelBoostCoal", 1.35D, 1.0D, 10.0D);
        EXTRACTOR_FUEL_BOOST_LAVA = builder
                .comment("Production-speed boost while piped or bucketed lava fuel is active.")
                .defineInRange("fuelBoostLava", 1.75D, 1.0D, 10.0D);
        EXTRACTOR_FUEL_BOOST_LIQUID_FIRE = builder
                .comment("Production-speed boost while Liquid Fire fuel is active (top of ladder).")
                .defineInRange("fuelBoostLiquidFire", 2.5D, 1.0D, 10.0D);
        EXTRACTOR_FUEL_BURN_CHARCOAL = builder
                .comment("Ticks one charcoal lasts in the solid fuel slot.")
                .defineInRange("fuelBurnCharcoal", 1600, 20, 120000);
        EXTRACTOR_FUEL_BURN_COAL = builder
                .comment("Ticks one coal lasts in the solid fuel slot.")
                .defineInRange("fuelBurnCoal", 2400, 20, 120000);
        EXTRACTOR_FUEL_BURN_LAVA = builder
                .comment("Ticks one lava bucket lasts in the solid fuel slot.")
                .defineInRange("fuelBurnLava", 20000, 20, 120000);
        EXTRACTOR_FUEL_BURN_LIQUID_FIRE = builder
                .comment("Ticks one Liquid Fire bucket lasts in the solid fuel slot.")
                .defineInRange("fuelBurnLiquidFire", 36000, 20, 120000);
        EXTRACTOR_LAVA_TANK_CAPACITY = builder
                .comment("Internal lava tank capacity in millibuckets.")
                .defineInRange("lavaTankCapacity", 8000, 1000, 64000);
        EXTRACTOR_LAVA_CONSUMPTION_MB = builder
                .comment("Lava millibuckets consumed per production tick while lava boost is active.")
                .defineInRange("lavaConsumptionMb", 5, 1, 1000);
        EXTRACTOR_BUFFER_SLOTS = builder
                .comment("Output buffer inventory size (backpressure when full).")
                .defineInRange("bufferSlots", 27, 1, 128);
        EXTRACTOR_BUFFER_STACK_LIMIT = builder
                .comment("Max stack size per output buffer slot.")
                .defineInRange("bufferStackLimit", 64, 1, 64);
        EXTRACTOR_RAW_ORE_BLOCK_CHANCE = builder
                .comment("Chance per output roll to produce a raw ore block instead of processable ore.")
                .defineInRange("rawOreBlockChance", 0.08D, 0.0D, 1.0D);
        EXTRACTOR_DECAY_INTERVAL_TICKS = builder
                .comment("Ticks between buffer decay steps while unpowered.")
                .defineInRange("decayIntervalTicks", 200, 20, 12000);
        EXTRACTOR_DECAY_AMOUNT = builder
                .comment("Items removed from the buffer per decay step while unpowered.")
                .defineInRange("decayAmount", 1, 1, 64);
        EXTRACTOR_MIN_ROTATION_SPEED = builder
                .comment("Minimum Create neighbor RPM required to run production.")
                .defineInRange("minRotationSpeed", 16.0D, 0.0D, 512.0D);
        EXTRACTOR_STRUCTURE_CHECK_INTERVAL = builder
                .comment("Ticks between multiblock structure revalidation checks.")
                .defineInRange("structureCheckInterval", 40, 5, 600);
        builder.pop();

        builder.push("noBuildAura");
        NO_BUILD_AURA_ENABLED = builder
                .comment("Deny block placement near ore nodes except whitelisted extractor parts (keeps taps exposed).")
                .define("enabled", true);
        NO_BUILD_AURA_RADIUS = builder
                .comment("Radius in blocks from each ore-node center where non-whitelisted placement is blocked.")
                .defineInRange("radius", 24, 4, 128);
        NO_BUILD_AURA_WHITELIST = builder
                .comment("Block ids allowed inside the no-build aura (extensible list).")
                .defineList("whitelist", List.of(
                        "nodecore:ore_extractor_core",
                        "nodecore:ore_extractor_frame"
                ), o -> o instanceof String);
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
        MARKER_DEFAULT_NODE_TYPE = builder
                .comment("Node type registered when a node_marker block is placed.")
                .define("markerDefaultType", "ore_iron");
        builder.pop();

        builder.push("oreWorldgen");
        ORE_NODE_WORLDGEN_ENABLED = builder
                .comment("Enable config-driven ore-node worldgen (clustered metal types + tiers).")
                .define("enabled", true);
        ORE_NODE_FREQUENCY = builder
                .comment("Fraction of valid grid slots that receive an ore node (0.0-1.0).")
                .defineInRange("frequency", 0.85D, 0.0D, 1.0D);
        ORE_NODE_CLUSTER_SIZE = builder
                .comment("Edge length (blocks) of a geographic metal-type cluster region.")
                .defineInRange("clusterSize", 8192, 2048, 32000);
        ORE_NODE_MIN_Y = builder
                .comment("Minimum Y for ore-node fixture placement.")
                .defineInRange("minY", -32, -64, 320);
        ORE_NODE_MAX_Y = builder
                .comment("Maximum Y for ore-node fixture placement.")
                .defineInRange("maxY", 96, -64, 320);
        ORE_NODE_TIER_1_WEIGHT = builder
                .comment("Relative weight for tier-1 output richness.")
                .defineInRange("tier1Weight", 50, 0, 1000);
        ORE_NODE_TIER_2_WEIGHT = builder
                .comment("Relative weight for tier-2 output richness.")
                .defineInRange("tier2Weight", 35, 0, 1000);
        ORE_NODE_TIER_3_WEIGHT = builder
                .comment("Relative weight for tier-3 output richness.")
                .defineInRange("tier3Weight", 15, 0, 1000);
        ORE_NODE_METAL_TYPES = builder
                .comment("Ore node types used for cluster hashing when no biome mapping matches.")
                .defineList("metalTypes", List.of(
                        "ore_iron",
                        "ore_copper",
                        "ore_brass",
                        "ore_quartz"
                ), o -> o instanceof String);
        ORE_NODE_BIOME_METAL_TYPES = builder
                .comment("Biome-keyed metal selection (biome_id=node_type). Creates geographic metal regions.")
                .defineList("biomeMetalTypes", List.of(
                        "minecraft:plains=ore_iron",
                        "minecraft:sunflower_plains=ore_iron",
                        "minecraft:forest=ore_iron",
                        "minecraft:birch_forest=ore_iron",
                        "minecraft:dark_forest=ore_iron",
                        "minecraft:taiga=ore_iron",
                        "minecraft:snowy_taiga=ore_iron",
                        "minecraft:windswept_hills=ore_iron",
                        "minecraft:windswept_gravelly_hills=ore_iron",
                        "minecraft:desert=ore_copper",
                        "minecraft:badlands=ore_copper",
                        "minecraft:eroded_badlands=ore_copper",
                        "minecraft:wooded_badlands=ore_copper",
                        "minecraft:savanna=ore_copper",
                        "minecraft:savanna_plateau=ore_copper",
                        "minecraft:jungle=ore_brass",
                        "minecraft:sparse_jungle=ore_brass",
                        "minecraft:bamboo_jungle=ore_brass",
                        "minecraft:swamp=ore_brass",
                        "minecraft:mangrove_swamp=ore_brass",
                        "minecraft:meadow=ore_quartz",
                        "minecraft:cherry_grove=ore_quartz",
                        "minecraft:stony_peaks=ore_quartz",
                        "minecraft:frozen_peaks=ore_quartz",
                        "minecraft:jagged_peaks=ore_quartz"
                ), o -> o instanceof String);
        builder.pop();

        builder.push("incontrol");
        SPAWN_EXPORT_ENABLED = builder
                .comment("Allow /nodecore export incontrol to write In Control helper rule files.")
                .define("spawnExportEnabled", true);
        SPAWN_MOBS_BY_TYPE = builder
                .comment("Mob id per node type for In Control spawner export (type=mob_id).")
                .defineList("spawnMobsByType", List.of(
                        "ore_iron=minecraft:zombie",
                        "ore_copper=minecraft:husk",
                        "ore_brass=minecraft:drowned",
                        "ore_quartz=minecraft:zombified_piglin",
                        "lush_hydro=minecraft:cave_spider",
                        "quartz_rift=minecraft:blaze"
                ), o -> o instanceof String);
        builder.pop();

        builder.push("lushCavern");
        LUSH_CAVERN_WORLDGEN_ENABLED = builder
                .comment("Enable massive lush underground cavern worldgen (below Y 0 only).")
                .define("enabled", true);
        LUSH_CAVERN_FREQUENCY = builder
                .comment("Fraction of valid grid slots that receive a lush cavern (0.0-1.0).")
                .defineInRange("frequency", 0.12D, 0.0D, 1.0D);
        LUSH_CAVERN_GRID_SPACING = builder
                .comment("Spacing in blocks between lush cavern anchor grid points.")
                .defineInRange("gridSpacing", 2048, 512, 12000);
        LUSH_CAVERN_MIN_Y = builder
                .comment("Minimum Y bound for cavern volume (must stay below 0).")
                .defineInRange("minY", -56, -64, -1);
        LUSH_CAVERN_MAX_Y = builder
                .comment("Maximum Y bound for cavern volume (must stay below 0).")
                .defineInRange("maxY", -8, -64, -1);
        LUSH_CAVERN_MIN_HORIZONTAL_RADIUS = builder
                .comment("Minimum horizontal radius (X/Z) for generated caverns.")
                .defineInRange("minHorizontalRadius", 48, 16, 256);
        LUSH_CAVERN_MAX_HORIZONTAL_RADIUS = builder
                .comment("Maximum horizontal radius (X/Z) for generated caverns.")
                .defineInRange("maxHorizontalRadius", 96, 24, 320);
        LUSH_CAVERN_MIN_VERTICAL_RADIUS = builder
                .comment("Minimum vertical radius (Y) for generated caverns.")
                .defineInRange("minVerticalRadius", 24, 8, 160);
        LUSH_CAVERN_MAX_VERTICAL_RADIUS = builder
                .comment("Maximum vertical radius (Y) for generated caverns.")
                .defineInRange("maxVerticalRadius", 40, 12, 200);
        LUSH_CAVERN_FLORA_DENSITY = builder
                .comment("Chance per floor sample to place grass/ferns inside caverns.")
                .defineInRange("floraDensity", 0.35D, 0.0D, 1.0D);
        LUSH_CAVERN_TREE_DENSITY = builder
                .comment("Chance per floor sample to place a tree (Dynamic Trees when present, else vanilla sapling).")
                .defineInRange("treeDensity", 0.08D, 0.0D, 1.0D);
        LUSH_CAVERN_MOSS_DENSITY = builder
                .comment("Chance per floor sample to place moss carpet.")
                .defineInRange("mossDensity", 0.25D, 0.0D, 1.0D);
        LUSH_CAVERN_REGISTER_TERRITORY = builder
                .comment("Register each cavern as lush_hydro held territory for spawn/export hooks.")
                .define("registerTerritory", true);
        LUSH_CAVERN_TERRITORY_PADDING = builder
                .comment("Extra radius added when registering cavern territory nodes.")
                .defineInRange("territoryPadding", 16, 0, 128);
        builder.pop();

        builder.push("liquidFire");
        LIQUID_FIRE_COLLECTION_ENABLED = builder
                .comment("Enable cauldron-under-soul-sand-with-lava Liquid Fire collection.")
                .define("collectionEnabled", true);
        LIQUID_FIRE_COLLECTION_INTERVAL_TICKS = builder
                .comment("Ticks between collection progress updates for active cauldrons.")
                .defineInRange("collectionIntervalTicks", 40, 5, 600);
        LIQUID_FIRE_COLLECTION_FILL_TICKS = builder
                .comment("Total ticks required to fill a collector cauldron (interval * ticks / interval).")
                .defineInRange("collectionFillTicks", 600, 40, 12000);
        LIQUID_FIRE_TINT = builder
                .comment("ARGB hex tint for Liquid Fire fluid rendering (#RRGGBB or #AARRGGBB).")
                .define("tint", "#00FBFF");
        LIQUID_FIRE_FUEL_BURN_TIME = builder
                .comment("Furnace and extractor fuel burn time for a Liquid Fire bucket (top of fuel ladder).")
                .defineInRange("fuelBurnTime", 36000, 32000, 40000);
        LIQUID_FIRE_PARTICLE_CHANCE = builder
                .comment("Random denominator for SOUL_FIRE_FLAME particles on placed fluid (1 = every tick).")
                .defineInRange("particleChance", 5, 1, 40);
        builder.pop();

        COMMON_SPEC = builder.build();
    }

    public static int liquidFireTintArgb() {
        return parseColorHex(LIQUID_FIRE_TINT.get(), 0xFF00FBFF);
    }

    private static int parseColorHex(String raw, int fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        String hex = raw.trim();
        if (hex.startsWith("#")) {
            hex = hex.substring(1);
        }
        if (hex.length() == 6) {
            hex = "FF" + hex;
        }
        if (hex.length() != 8) {
            return fallback;
        }
        try {
            return (int) Long.parseLong(hex, 16);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private NodeCoreConfig() {}
}