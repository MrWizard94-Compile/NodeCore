package com.mrwizard94.nodecore.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;

public class LiquidFireCollectionData extends SavedData {
    private static final String DATA_NAME = "nodecore_liquid_fire_collectors";

    private final Map<BlockPos, CollectorState> collectors = new HashMap<>();

    public static LiquidFireCollectionData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(
                LiquidFireCollectionData::load,
                LiquidFireCollectionData::new,
                DATA_NAME
        );
    }

    public LiquidFireCollectionData() {
    }

    public void track(BlockPos cauldronPos) {
        collectors.putIfAbsent(cauldronPos.immutable(), new CollectorState(0, false));
        setDirty();
    }

    public void untrack(BlockPos cauldronPos) {
        if (collectors.remove(cauldronPos) != null) {
            setDirty();
        }
    }

    public boolean isTracked(BlockPos cauldronPos) {
        return collectors.containsKey(cauldronPos);
    }

    public boolean isReady(BlockPos cauldronPos) {
        CollectorState state = collectors.get(cauldronPos);
        return state != null && state.ready;
    }

    public int progressAt(BlockPos cauldronPos) {
        CollectorState state = collectors.get(cauldronPos);
        return state == null ? 0 : state.progressTicks;
    }

    public void setProgress(BlockPos cauldronPos, int progressTicks, boolean ready) {
        CollectorState state = collectors.computeIfAbsent(cauldronPos.immutable(), ignored -> new CollectorState(0, false));
        state.progressTicks = Math.max(0, progressTicks);
        state.ready = ready;
        setDirty();
    }

    public void reset(BlockPos cauldronPos) {
        collectors.put(cauldronPos.immutable(), new CollectorState(0, false));
        setDirty();
    }

    public Map<BlockPos, CollectorState> snapshot() {
        return Map.copyOf(collectors);
    }

    public static LiquidFireCollectionData load(CompoundTag tag) {
        LiquidFireCollectionData data = new LiquidFireCollectionData();
        ListTag list = tag.getList("Collectors", Tag.TAG_COMPOUND);
        for (Tag entry : list) {
            CompoundTag collectorTag = (CompoundTag) entry;
            BlockPos pos = BlockPos.of(collectorTag.getLong("Pos"));
            int progress = collectorTag.getInt("Progress");
            boolean ready = collectorTag.getBoolean("Ready");
            data.collectors.put(pos, new CollectorState(progress, ready));
        }
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, CollectorState> entry : collectors.entrySet()) {
            CompoundTag collectorTag = new CompoundTag();
            collectorTag.putLong("Pos", entry.getKey().asLong());
            collectorTag.putInt("Progress", entry.getValue().progressTicks);
            collectorTag.putBoolean("Ready", entry.getValue().ready);
            list.add(collectorTag);
        }
        tag.put("Collectors", list);
        return tag;
    }

    public static final class CollectorState {
        public int progressTicks;
        public boolean ready;

        public CollectorState(int progressTicks, boolean ready) {
            this.progressTicks = progressTicks;
            this.ready = ready;
        }
    }
}