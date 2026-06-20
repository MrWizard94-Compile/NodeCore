package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.node.NodeQueries;
import com.mrwizard94.nodecore.node.ResourceNode;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Optional;

public class ExtractionAlertHandler {
    @SubscribeEvent
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!NodeCoreConfig.EXTRACTION_ALERTS_ENABLED.get()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        BlockState placed = event.getPlacedBlock();
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(placed.getBlock());
        if (blockId == null || !isAlertBlock(blockId)) {
            return;
        }

        BlockPos pos = event.getPos();
        Optional<ResourceNode> oreNode = NodeQueries.nodeAt(serverLevel, pos)
                .filter(node -> node.getType().isOre());

        if (oreNode.isEmpty()) {
            return;
        }

        String playerName = "Unknown";
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer player) {
            playerName = player.getGameProfile().getName();
        }

        ResourceNode node = oreNode.get();
        Component alert = Component.translatable(
                "nodecore.message.extraction_alert",
                playerName,
                pos.getX(),
                pos.getZ()).withStyle(ChatFormatting.RED, ChatFormatting.BOLD);

        for (ServerPlayer player : serverLevel.players()) {
            player.displayClientMessage(alert, false);
        }
    }

    private static boolean isAlertBlock(ResourceLocation blockId) {
        for (String configured : NodeCoreConfig.EXTRACTION_ALERT_BLOCKS.get()) {
            if (blockId.toString().equals(configured)) {
                return true;
            }
        }
        return false;
    }
}