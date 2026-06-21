package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.node.ResourceNode;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = NodeCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class OreNodeNoBuildAuraHandler {
    private OreNodeNoBuildAuraHandler() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!NodeCoreConfig.NO_BUILD_AURA_ENABLED.get()) {
            return;
        }
        if (event.getLevel().isClientSide()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockState placed = event.getPlacedBlock();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(placed.getBlock());
        if (blockId != null && isWhitelisted(blockId)) {
            return;
        }

        int auraRadius = NodeCoreConfig.NO_BUILD_AURA_RADIUS.get();
        BlockPos pos = event.getPos();
        boolean blocked = NodeSavedData.get(serverLevel).within(pos, auraRadius).stream()
                .map(ResourceNode::getType)
                .anyMatch(type -> type.isOre());
        if (!blocked) {
            return;
        }

        event.setCanceled(true);
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer player) {
            player.displayClientMessage(
                    Component.translatable("nodecore.message.no_build_aura"),
                    true);
        }
    }

    private static boolean isWhitelisted(ResourceLocation blockId) {
        for (String configured : NodeCoreConfig.NO_BUILD_AURA_WHITELIST.get()) {
            if (blockId.toString().equals(configured)) {
                return true;
            }
        }
        return false;
    }
}