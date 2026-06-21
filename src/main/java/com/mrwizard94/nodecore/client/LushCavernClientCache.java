package com.mrwizard94.nodecore.client;

import com.mrwizard94.nodecore.data.LushCavernVolume;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * Client-side mirror of registered lush cavern volumes (synced from server).
 */
public final class LushCavernClientCache {
    private static final List<LushCavernVolume> VOLUMES = new ArrayList<>();

    private LushCavernClientCache() {
    }

    public static boolean contains(BlockPos pos) {
        for (LushCavernVolume volume : VOLUMES) {
            if (volume.contains(pos)) {
                return true;
            }
        }
        return false;
    }

    public static void replaceAll(List<LushCavernVolume> volumes) {
        VOLUMES.clear();
        VOLUMES.addAll(volumes);
    }

    public static void addAll(List<LushCavernVolume> volumes) {
        for (LushCavernVolume volume : volumes) {
            boolean replaced = false;
            for (int i = 0; i < VOLUMES.size(); i++) {
                if (VOLUMES.get(i).getId().equals(volume.getId())) {
                    VOLUMES.set(i, volume);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                VOLUMES.add(volume);
            }
        }
    }

    public static List<LushCavernVolume> snapshot() {
        return Collections.unmodifiableList(VOLUMES);
    }

    public static void clearForLogout() {
        VOLUMES.clear();
    }
}