package com.mrwizard94.nodecore.datagen;

import com.google.gson.JsonObject;
import com.mrwizard94.nodecore.NodeCore;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public class NodeSpacingWorldgenProvider implements DataProvider {
    private static final int DEFAULT_MIN_SPACING = 2500;
    private static final int DEFAULT_MAX_SPACING = 4000;

    private final PackOutput output;

    public NodeSpacingWorldgenProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        JsonObject json = new JsonObject();
        json.addProperty("min_spacing", DEFAULT_MIN_SPACING);
        json.addProperty("max_spacing", DEFAULT_MAX_SPACING);
        json.addProperty("status", "stub");
        json.addProperty("integration", "large_ore_deposits_pending");
        json.addProperty("notes",
                "Runtime spacing uses NodeCoreConfig minSpacing/maxSpacing; this file documents pack defaults.");

        Path path = output.getOutputFolder()
                .resolve(NodeCore.MOD_ID)
                .resolve("worldgen")
                .resolve("node_spacing_hints.json");

        return DataProvider.saveStable(cache, json, path);
    }

    @Override
    public String getName() {
        return "Node Core Worldgen Spacing Hints";
    }
}