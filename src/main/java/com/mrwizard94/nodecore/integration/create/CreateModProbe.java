package com.mrwizard94.nodecore.integration.create;

import net.minecraftforge.fml.ModList;

public final class CreateModProbe {
    private static final String CREATE_MOD_ID = "create";

    private CreateModProbe() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(CREATE_MOD_ID);
    }
}