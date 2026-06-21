package com.mrwizard94.nodecore.network;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.data.LushCavernSavedData;
import com.mrwizard94.nodecore.data.LushCavernVolume;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;

public final class NodeCoreNetwork {
    private static final String PROTOCOL = "1";
    private static final ResourceLocation CHANNEL_ID = new ResourceLocation(NodeCore.MOD_ID, "main");

    private static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            CHANNEL_ID,
            () -> PROTOCOL,
            PROTOCOL::equals,
            PROTOCOL::equals
    );

    private static int packetId;

    private NodeCoreNetwork() {
    }

    public static void register() {
        CHANNEL.messageBuilder(LushCavernVolumesPacket.class, nextId(), NetworkDirection.PLAY_TO_CLIENT)
                .encoder(LushCavernVolumesPacket::encode)
                .decoder(LushCavernVolumesPacket::decode)
                .consumerMainThread(LushCavernVolumesPacket::handle)
                .add();
    }

    public static void syncAllCaverns(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new LushCavernVolumesPacket(LushCavernSavedData.get(level).getVolumes(), true));
    }

    public static void syncCavernVolume(ServerLevel level, LushCavernVolume volume) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), new LushCavernVolumesPacket(List.of(volume), false));
    }

    public static void syncCavernsForChunk(ServerLevel level, ServerPlayer player, int chunkX, int chunkZ) {
        List<LushCavernVolume> volumes = LushCavernSavedData.get(level).intersectingChunk(chunkX, chunkZ);
        if (volumes.isEmpty()) {
            return;
        }
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player),
                new LushCavernVolumesPacket(volumes, false));
    }

    private static int nextId() {
        return packetId++;
    }
}