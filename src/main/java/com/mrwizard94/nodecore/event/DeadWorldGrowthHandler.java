package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.node.NodeQueries;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NodeCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class DeadWorldGrowthHandler {
    private DeadWorldGrowthHandler() {}

    @SubscribeEvent
    public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        if (!shouldGateGrowth(event.getLevel())) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (isAllowedGrowthZone(level, event.getPos())) {
            return;
        }
        event.setResult(Event.Result.DENY);
    }

    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (!shouldGateGrowth(event.getLevel())) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        BlockPos pos = event.getPos();
        if (isAllowedGrowthZone(level, pos)) {
            return;
        }

        event.setCanceled(true);
        Player player = event.getEntity();
        if (player != null) {
            player.displayClientMessage(Component.translatable("nodecore.message.growth_lush_only"), true);
        }
    }

    private static boolean isAllowedGrowthZone(ServerLevel level, BlockPos pos) {
        return NodeQueries.isInLushCavern(level, pos) || NodeQueries.isInLushNode(level, pos);
    }

    private static boolean shouldGateGrowth(LevelAccessor level) {
        if (level.isClientSide()) {
            return false;
        }
        return NodeCoreConfig.DEAD_WORLD_ENABLED.get()
                && NodeCoreConfig.DEAD_WORLD_GROWTH_LUSH_ONLY.get();
    }
}