package com.mrwizard94.nodecore.integration;

import com.mrwizard94.nodecore.event.ExtractionAlertEvent;
import net.minecraftforge.fml.ModList;

/**
 * Soft-optional bridge to KubeJS. No compile-time dependency on KubeJS;
 * presence is detected at runtime via {@link ModList}.
 * <p>
 * Pack scripts listen with:
 * {@code ForgeEvents.onEvent('com.mrwizard94.nodecore.event.ExtractionAlertEvent', event => { ... })}
 */
public final class NodeKubeBridge {
    private static final String[] KUBEJS_MOD_IDS = {"kubejs"};

    /** Fully qualified class name for KubeJS {@code ForgeEvents.onEvent(...)} handlers. */
    public static final String EXTRACTION_ALERT_EVENT_CLASS =
            "com.mrwizard94.nodecore.event.ExtractionAlertEvent";

    private NodeKubeBridge() {}

    public static boolean isKubeJsPresent() {
        for (String modId : KUBEJS_MOD_IDS) {
            if (ModList.get().isLoaded(modId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return event class name for KubeJS {@code ForgeEvents.onEvent(...)} handlers
     */
    public static String extractionAlertEventClassName() {
        return EXTRACTION_ALERT_EVENT_CLASS;
    }

    /**
     * @return simple reference to the Java event type (for documentation / logging)
     */
    public static Class<ExtractionAlertEvent> extractionAlertEventType() {
        return ExtractionAlertEvent.class;
    }
}