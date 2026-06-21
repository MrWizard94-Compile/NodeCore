package com.mrwizard94.nodecore.integration.dynamictrees;

import net.minecraftforge.fml.ModList;

public final class DynamicTreesModProbe {
    private static final String DYNAMIC_TREES_MOD_ID = "dynamictrees";

    private DynamicTreesModProbe() {
    }

    public static boolean isLoaded() {
        return ModList.get().isLoaded(DYNAMIC_TREES_MOD_ID);
    }
}