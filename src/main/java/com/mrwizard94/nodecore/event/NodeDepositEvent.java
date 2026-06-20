package com.mrwizard94.nodecore.event;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when a Large Ore Deposit (or compatible worldgen hook) is placed.
 * Optional LOD integration posts this at runtime; {@link com.mrwizard94.nodecore.event.NodeLodEventHandler}
 * links the deposit center into {@link com.mrwizard94.nodecore.data.NodeSavedData}.
 */
public class NodeDepositEvent extends Event {
    private final ServerLevel level;
    private final BlockPos pos;
    private final String depositType;

    public NodeDepositEvent(ServerLevel level, BlockPos pos, String depositType) {
        this.level = level;
        this.pos = pos;
        this.depositType = depositType;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public String getDepositType() {
        return depositType;
    }
}