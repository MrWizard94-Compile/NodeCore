package com.mrwizard94.nodecore.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class LushCavernSavedData extends SavedData {
    private static final String DATA_NAME = "nodecore_lush_caverns";
    private final List<LushCavernVolume> volumes = new ArrayList<>();

    public static LushCavernSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                LushCavernSavedData::load,
                LushCavernSavedData::new,
                DATA_NAME
        );
    }

    public LushCavernSavedData() {
    }

    public List<LushCavernVolume> getVolumes() {
        return Collections.unmodifiableList(volumes);
    }

    public boolean contains(BlockPos pos) {
        for (LushCavernVolume volume : volumes) {
            if (volume.contains(pos)) {
                return true;
            }
        }
        return false;
    }

    public List<LushCavernVolume> intersectingChunk(int chunkX, int chunkZ) {
        List<LushCavernVolume> matches = new ArrayList<>();
        for (LushCavernVolume volume : volumes) {
            if (volume.intersectsChunk(chunkX, chunkZ)) {
                matches.add(volume);
            }
        }
        return matches;
    }

    public Optional<LushCavernVolume> findById(UUID id) {
        return volumes.stream().filter(volume -> volume.getId().equals(id)).findFirst();
    }

    public boolean hasVolumeNear(BlockPos center, int tolerance) {
        long toleranceSq = (long) tolerance * tolerance;
        for (LushCavernVolume volume : volumes) {
            if (volume.center().distSqr(center) <= toleranceSq) {
                return true;
            }
        }
        return false;
    }

    public LushCavernVolume register(LushCavernVolume volume) {
        if (findById(volume.getId()).isPresent()) {
            return volume;
        }
        volumes.add(volume);
        setDirty();
        return volume;
    }

    public static LushCavernSavedData load(CompoundTag tag) {
        LushCavernSavedData data = new LushCavernSavedData();
        ListTag list = tag.getList("Volumes", Tag.TAG_COMPOUND);
        for (Tag entry : list) {
            data.volumes.add(LushCavernVolume.load((CompoundTag) entry));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (LushCavernVolume volume : volumes) {
            list.add(volume.save());
        }
        tag.put("Volumes", list);
        return tag;
    }
}