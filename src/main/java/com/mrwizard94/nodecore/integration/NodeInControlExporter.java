package com.mrwizard94.nodecore.integration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.node.NodeType;
import com.mrwizard94.nodecore.node.ResourceNode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.loading.FMLPaths;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Exports In Control rule snippets from the node registry for the current dimension.
 * Writes helper files under {@code <gameDir>/config/incontrol/} for pack merge into
 * {@code areas.json}, {@code spawner.json}, and {@code spawn.json}.
 */
public final class NodeInControlExporter {
    public static final String AREAS_FILE_NAME = "nodecore_generated_areas.json";
    public static final String SPAWNER_FILE_NAME = "nodecore_generated_spawner.json";

    private static final int FULL_HEIGHT_MIN = 0;
    private static final int FULL_HEIGHT_MAX = 320;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final Map<NodeType, SpawnerTemplate> SPAWNER_TEMPLATES = Map.of(
            NodeType.ORE_IRON, new SpawnerTemplate(0.15f, 2, 1, 2, 6, true),
            NodeType.ORE_COPPER, new SpawnerTemplate(0.12f, 2, 1, 2, 5, true),
            NodeType.ORE_BRASS, new SpawnerTemplate(0.12f, 2, 1, 3, 5, true),
            NodeType.ORE_QUARTZ, new SpawnerTemplate(0.18f, 3, 1, 3, 8, false),
            NodeType.LUSH_HYDRO, new SpawnerTemplate(0.08f, 1, 1, 2, 4, true),
            NodeType.QUARTZ_RIFT, new SpawnerTemplate(0.2f, 3, 1, 4, 8, false)
    );

    private static final List<String> DEFAULT_MOB_MAPPINGS = List.of(
            "ore_iron=minecraft:zombie",
            "ore_copper=minecraft:husk",
            "ore_brass=minecraft:drowned",
            "ore_quartz=minecraft:zombified_piglin",
            "lush_hydro=minecraft:cave_spider",
            "quartz_rift=minecraft:blaze"
    );

    private NodeInControlExporter() {}

    public record ExportResult(Path areasPath, Path spawnerPath, int nodeCount) {}

    private record SpawnerTemplate(float perSecond, int attempts, int minSpawn, int maxSpawn, int maxLocal,
                                     boolean cave) {}

    private record Bounds(String dimension, int xmin, int xmax, int ymin, int ymax, int zmin, int zmax) {}

    public static ExportResult export(ServerLevel level) throws IOException {
        if (!NodeCoreConfig.SPAWN_EXPORT_ENABLED.get()) {
            throw new IllegalStateException("spawn export disabled in config");
        }

        List<ResourceNode> nodes = NodeSavedData.get(level).getNodes();
        Map<String, String> mobByType = resolveMobMappings();

        JsonArray areas = new JsonArray();
        JsonArray spawnerRules = new JsonArray();

        for (ResourceNode node : nodes) {
            String areaName = areaName(node);
            Bounds bounds = boundsFor(level, node);

            areas.add(buildArea(areaName, bounds));

            String mobId = mobByType.getOrDefault(node.getType().getId(), "minecraft:zombie");
            SpawnerTemplate template = SPAWNER_TEMPLATES.getOrDefault(
                    node.getType(), SPAWNER_TEMPLATES.get(NodeType.ORE_IRON));

            spawnerRules.add(buildSpawnerAddRule(mobId, bounds, template));
            spawnerRules.add(buildSpawnAreaFilterRule(mobId, areaName));
        }

        Path outputDir = FMLPaths.GAMEDIR.get().resolve("config").resolve("incontrol");
        Files.createDirectories(outputDir);

        Path areasPath = outputDir.resolve(AREAS_FILE_NAME);
        Path spawnerPath = outputDir.resolve(SPAWNER_FILE_NAME);

        writeJson(areasPath, areas);
        writeJson(spawnerPath, spawnerRules);

        NodeCore.LOGGER.info("Exported {} In Control node areas to {}", nodes.size(), areasPath);
        NodeCore.LOGGER.info("Exported {} In Control spawner/spawn snippets to {}", spawnerRules.size(), spawnerPath);

        return new ExportResult(areasPath, spawnerPath, nodes.size());
    }

    public static String areaName(ResourceNode node) {
        return "nodecore_" + node.getType().getId() + "_" + node.getId().toString().substring(0, 8);
    }

    private static Bounds boundsFor(ServerLevel level, ResourceNode node) {
        BlockPos center = node.getCenter();
        int radius = node.getRadius();
        int ymin;
        int ymax;
        if (node.getType() == NodeType.LUSH_HYDRO) {
            ymin = center.getY() - radius;
            ymax = center.getY() + radius;
        } else {
            ymin = FULL_HEIGHT_MIN;
            ymax = FULL_HEIGHT_MAX;
        }

        return new Bounds(
                level.dimension().location().toString(),
                center.getX() - radius,
                center.getX() + radius,
                ymin,
                ymax,
                center.getZ() - radius,
                center.getZ() + radius
        );
    }

    private static JsonObject buildArea(String areaName, Bounds bounds) {
        JsonObject area = new JsonObject();
        area.addProperty("name", areaName);
        area.addProperty("dimension", bounds.dimension());
        area.addProperty("xmin", bounds.xmin());
        area.addProperty("xmax", bounds.xmax());
        area.addProperty("ymin", bounds.ymin());
        area.addProperty("ymax", bounds.ymax());
        area.addProperty("zmin", bounds.zmin());
        area.addProperty("zmax", bounds.zmax());
        return area;
    }

    private static JsonObject buildSpawnerAddRule(String mobId, Bounds bounds, SpawnerTemplate template) {
        JsonObject amount = new JsonObject();
        amount.addProperty("minimum", template.minSpawn());
        amount.addProperty("maximum", template.maxSpawn());

        JsonObject conditions = new JsonObject();
        conditions.addProperty("dimension", bounds.dimension());
        conditions.addProperty("minheight", bounds.ymin());
        conditions.addProperty("maxheight", bounds.ymax());
        conditions.addProperty("maxlocal", template.maxLocal());
        if (template.cave()) {
            conditions.addProperty("cave", true);
        }

        JsonObject rule = new JsonObject();
        rule.addProperty("mob", mobId);
        rule.addProperty("persecond", template.perSecond());
        rule.addProperty("attempts", template.attempts());
        rule.add("amount", amount);
        rule.add("conditions", conditions);
        return rule;
    }

    /**
     * Spawn.json-style filter that ties In Control spawner output to a named node area.
     */
    private static JsonObject buildSpawnAreaFilterRule(String mobId, String areaName) {
        JsonObject rule = new JsonObject();
        rule.addProperty("mob", mobId);
        rule.addProperty("area", areaName);
        rule.addProperty("incontrol", true);
        rule.addProperty("result", "allow");
        return rule;
    }

    private static Map<String, String> resolveMobMappings() {
        Map<String, String> mappings = new HashMap<>();
        for (String entry : DEFAULT_MOB_MAPPINGS) {
            putMapping(mappings, entry);
        }
        for (String entry : NodeCoreConfig.SPAWN_MOBS_BY_TYPE.get()) {
            putMapping(mappings, entry);
        }
        return mappings;
    }

    private static void putMapping(Map<String, String> mappings, String entry) {
        if (entry == null || entry.isBlank()) {
            return;
        }
        int separator = entry.indexOf('=');
        if (separator <= 0 || separator >= entry.length() - 1) {
            NodeCore.LOGGER.warn("Ignoring invalid spawn mob mapping '{}'", entry);
            return;
        }
        mappings.put(entry.substring(0, separator).trim(), entry.substring(separator + 1).trim());
    }

    private static void writeJson(Path path, JsonArray json) throws IOException {
        try (Writer writer = Files.newBufferedWriter(path)) {
            GSON.toJson(json, writer);
            writer.write(System.lineSeparator());
        }
    }
}