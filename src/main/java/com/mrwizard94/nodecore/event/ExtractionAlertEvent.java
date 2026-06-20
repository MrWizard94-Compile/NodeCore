package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.node.ResourceNode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Fired when a configured extraction block (e.g. Create mechanical drill) is placed inside an ore node.
 * Pack scripts via KubeJS can customize {@link #alertMessage} or cancel the event to suppress the broadcast.
 */
@Cancelable
public class ExtractionAlertEvent extends Event {
    private final ServerLevel level;
    private final BlockPos pos;
    private final ResourceNode node;
    @Nullable
    private final ServerPlayer player;
    private Component alertMessage;

    public ExtractionAlertEvent(
            ServerLevel level,
            BlockPos pos,
            ResourceNode node,
            @Nullable ServerPlayer player,
            Component alertMessage) {
        this.level = level;
        this.pos = pos;
        this.node = node;
        this.player = player;
        this.alertMessage = alertMessage;
    }

    /**
     * Posts this event on {@link MinecraftForge#EVENT_BUS} and returns the instance for inspection.
     */
    public static ExtractionAlertEvent post(
            ServerLevel level,
            BlockPos pos,
            ResourceNode node,
            @Nullable ServerPlayer player,
            Component alertMessage) {
        ExtractionAlertEvent event = new ExtractionAlertEvent(level, pos, node, player, alertMessage);
        MinecraftForge.EVENT_BUS.post(event);
        return event;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    public ResourceNode getNode() {
        return node;
    }

    @Nullable
    public ServerPlayer getPlayer() {
        return player;
    }

    public Component getAlertMessage() {
        return alertMessage;
    }

    public void setAlertMessage(Component alertMessage) {
        this.alertMessage = alertMessage;
    }
}