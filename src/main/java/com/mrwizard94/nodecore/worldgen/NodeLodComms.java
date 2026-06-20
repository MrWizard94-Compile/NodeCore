package com.mrwizard94.nodecore.worldgen;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.event.NodeDepositEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Receives {@code nodecore:register_deposit} IMC payloads from optional LOD companions.
 * Payload may be a {@link CompoundTag} ({@code X}, {@code Y}, {@code Z}, {@code Type}/{@code DepositType})
 * or a string {@code "x,y,z,depositType"}.
 */
public final class NodeLodComms {
    public static final String CHANNEL = "nodecore:register_deposit";

    private static final List<PendingDeposit> PENDING = new ArrayList<>();

    private NodeLodComms() {}

    public static void processImc(InterModProcessEvent event) {
        event.getIMCStream(CHANNEL::equals)
                .forEach(NodeLodComms::handleImcMessage);
    }

    private static void handleImcMessage(InterModComms.IMCMessage message) {
        PendingDeposit pending = parseMessage(message);
        if (pending == null) {
            NodeCore.LOGGER.warn("Invalid IMC payload on {} from {}", CHANNEL, message.senderModId());
            return;
        }

        if (postOrQueue(pending)) {
            NodeCore.LOGGER.debug("Processed IMC deposit '{}' at {} from {}",
                    pending.depositType(), pending.center(), message.senderModId());
        } else {
            PENDING.add(pending);
            NodeCore.LOGGER.debug("Queued IMC deposit '{}' at {} from {} until server start",
                    pending.depositType(), pending.center(), message.senderModId());
        }
    }

    @SubscribeEvent
    public static void onServerStarted(ServerStartedEvent event) {
        if (PENDING.isEmpty()) {
            return;
        }

        MinecraftServer server = event.getServer();
        List<PendingDeposit> remaining = new ArrayList<>();

        for (PendingDeposit pending : PENDING) {
            ServerLevel level = resolveLevel(server, pending.dimension());
            if (level == null) {
                remaining.add(pending);
                continue;
            }
            MinecraftForge.EVENT_BUS.post(new NodeDepositEvent(level, pending.center(), pending.depositType()));
        }

        PENDING.clear();
        PENDING.addAll(remaining);

        if (!PENDING.isEmpty()) {
            NodeCore.LOGGER.warn("{} queued LOD deposit registrations could not be resolved to a loaded dimension",
                    PENDING.size());
        }
    }

    public static boolean postOrQueue(PendingDeposit pending) {
        MinecraftServer server = getRunningServer();
        if (server == null) {
            return false;
        }

        ServerLevel level = resolveLevel(server, pending.dimension());
        if (level == null) {
            return false;
        }

        MinecraftForge.EVENT_BUS.post(new NodeDepositEvent(level, pending.center(), pending.depositType()));
        return true;
    }

    private static MinecraftServer getRunningServer() {
        return net.minecraftforge.server.ServerLifecycleHooks.getCurrentServer();
    }

    private static ServerLevel resolveLevel(MinecraftServer server, String dimension) {
        if (dimension == null || dimension.isBlank()) {
            return server.overworld();
        }

        ResourceKey<Level> key = ResourceKey.create(
                net.minecraft.core.registries.Registries.DIMENSION,
                net.minecraft.resources.ResourceLocation.tryParse(dimension));
        if (key == null) {
            return null;
        }
        return server.getLevel(key);
    }

    private static PendingDeposit parseMessage(InterModComms.IMCMessage message) {
        Supplier<?> supplier = message.messageSupplier();
        Object payload = supplier.get();

        if (payload instanceof CompoundTag tag) {
            return parseCompoundTag(tag);
        }
        if (payload instanceof String raw) {
            return parseString(raw);
        }
        return null;
    }

    private static PendingDeposit parseCompoundTag(CompoundTag tag) {
        if (!tag.contains("X", Tag.TAG_ANY_NUMERIC)
                || !tag.contains("Y", Tag.TAG_ANY_NUMERIC)
                || !tag.contains("Z", Tag.TAG_ANY_NUMERIC)) {
            return null;
        }

        String depositType = tag.contains("DepositType", Tag.TAG_STRING)
                ? tag.getString("DepositType")
                : tag.getString("Type");
        if (depositType.isBlank()) {
            return null;
        }

        String dimension = tag.contains("Dimension", Tag.TAG_STRING) ? tag.getString("Dimension") : null;
        BlockPos center = new BlockPos(tag.getInt("X"), tag.getInt("Y"), tag.getInt("Z"));
        return new PendingDeposit(center, depositType, dimension);
    }

    private static PendingDeposit parseString(String raw) {
        String[] parts = raw.split(",");
        if (parts.length < 4) {
            return null;
        }

        try {
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());
            int z = Integer.parseInt(parts[2].trim());
            String depositType = parts[3].trim();
            String dimension = parts.length > 4 ? parts[4].trim() : null;
            if (depositType.isEmpty()) {
                return null;
            }
            return new PendingDeposit(new BlockPos(x, y, z), depositType, dimension);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public record PendingDeposit(BlockPos center, String depositType, String dimension) {
        public PendingDeposit(BlockPos center, String depositType) {
            this(center, depositType, null);
        }
    }
}