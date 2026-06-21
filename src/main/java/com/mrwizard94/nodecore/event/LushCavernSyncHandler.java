package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.client.LushCavernClientCache;
import com.mrwizard94.nodecore.network.NodeCoreNetwork;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.ChunkWatchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = com.mrwizard94.nodecore.NodeCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LushCavernSyncHandler {
    private LushCavernSyncHandler() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        NodeCoreNetwork.syncAllCaverns(player);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity().level().isClientSide) {
            LushCavernClientCache.clearForLogout();
        }
    }

    @SubscribeEvent
    public static void onChunkWatch(ChunkWatchEvent.Watch event) {
        ServerPlayer player = event.getPlayer();
        ServerLevel level = player.serverLevel();
        NodeCoreNetwork.syncCavernsForChunk(level, player, event.getPos().x, event.getPos().z);
    }
}