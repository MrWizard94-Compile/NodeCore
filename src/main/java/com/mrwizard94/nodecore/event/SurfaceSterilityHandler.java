package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.util.OrganicBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class SurfaceSterilityHandler {
    @SubscribeEvent
    public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!NodeCoreConfig.SURFACE_STERILITY_ENABLED.get()) {
            return;
        }

        Level level = event.getLevel();
        if (level.isClientSide) {
            return;
        }

        ItemStack held = event.getItemStack();
        if (!OrganicBlocks.isPlantableItem(held)) {
            return;
        }

        BlockPos placePos = event.getPos().relative(event.getFace());
        if (!level.canSeeSky(placePos)) {
            return;
        }

        event.setCanceled(true);
        Player player = event.getEntity();
        if (player != null) {
            player.displayClientMessage(
                    Component.translatable("nodecore.message.surface_sterile"),
                    true);
        }
    }

    @SubscribeEvent
    public void onCropPlant(BlockEvent.EntityPlaceEvent event) {
        if (!NodeCoreConfig.SURFACE_STERILITY_ENABLED.get()) {
            return;
        }

        if (event.getLevel().isClientSide()) {
            return;
        }

        BlockState placed = event.getPlacedBlock();
        if (!OrganicBlocks.isOrganicBlockState(placed)) {
            return;
        }

        if (!event.getLevel().canSeeSky(event.getPos())) {
            return;
        }

        event.setCanceled(true);
    }
}