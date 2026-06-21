package com.mrwizard94.nodecore.network;

import com.mrwizard94.nodecore.client.LushCavernClientCache;
import com.mrwizard94.nodecore.data.LushCavernVolume;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public record LushCavernVolumesPacket(List<LushCavernVolume> volumes, boolean replaceAll) {

    public static void encode(LushCavernVolumesPacket packet, FriendlyByteBuf buf) {
        buf.writeBoolean(packet.replaceAll);
        buf.writeVarInt(packet.volumes.size());
        for (LushCavernVolume volume : packet.volumes) {
            volume.writeToBuffer(buf);
        }
    }

    public static LushCavernVolumesPacket decode(FriendlyByteBuf buf) {
        boolean replaceAll = buf.readBoolean();
        int count = buf.readVarInt();
        List<LushCavernVolume> volumes = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            volumes.add(LushCavernVolume.readFromBuffer(buf));
        }
        return new LushCavernVolumesPacket(volumes, replaceAll);
    }

    public static void handle(LushCavernVolumesPacket packet, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (packet.replaceAll) {
                LushCavernClientCache.replaceAll(packet.volumes);
            } else {
                LushCavernClientCache.addAll(packet.volumes);
            }
        });
        context.setPacketHandled(true);
    }
}