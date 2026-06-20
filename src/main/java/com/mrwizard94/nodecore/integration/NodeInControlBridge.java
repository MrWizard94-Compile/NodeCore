package com.mrwizard94.nodecore.integration;

import net.minecraftforge.fml.ModList;

/**
 * Soft-optional bridge to In Control. No compile-time dependency on In Control;
 * presence is detected at runtime via {@link ModList}.
 */
public final class NodeInControlBridge {
    private static final String[] IN_CONTROL_MOD_IDS = {"incontrol", "in_control"};

    private NodeInControlBridge() {}

    public static boolean isInControlPresent() {
        for (String modId : IN_CONTROL_MOD_IDS) {
            if (ModList.get().isLoaded(modId)) {
                return true;
            }
        }
        return false;
    }
}