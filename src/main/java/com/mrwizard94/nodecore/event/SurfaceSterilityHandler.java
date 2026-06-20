package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
        if (!isPlantable(held)) {
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
        if (!isCropOrSaplingBlock(placed.getBlock())) {
            return;
        }

        if (!event.getLevel().canSeeSky(event.getPos())) {
            return;
        }

        event.setCanceled(true);
    }

    private static boolean isPlantable(ItemStack stack) {
        return stack.is(Items.WHEAT_SEEDS)
                || stack.is(Items.BEETROOT_SEEDS)
                || stack.is(Items.MELON_SEEDS)
                || stack.is(Items.PUMPKIN_SEEDS)
                || stack.is(Items.CARROT)
                || stack.is(Items.POTATO)
                || stack.is(Items.NETHER_WART)
                || stack.is(Items.OAK_SAPLING)
                || stack.is(Items.SPRUCE_SAPLING)
                || stack.is(Items.BIRCH_SAPLING)
                || stack.is(Items.JUNGLE_SAPLING)
                || stack.is(Items.ACACIA_SAPLING)
                || stack.is(Items.DARK_OAK_SAPLING)
                || stack.is(Items.MANGROVE_PROPAGULE)
                || stack.is(Items.CHERRY_SAPLING)
                || stack.is(Items.SUGAR_CANE)
                || stack.is(Items.BAMBOO)
                || stack.is(Items.CACTUS)
                || stack.is(Items.SWEET_BERRIES);
    }

    private static boolean isCropOrSaplingBlock(Block block) {
        return block == Blocks.WHEAT
                || block == Blocks.BEETROOTS
                || block == Blocks.MELON_STEM
                || block == Blocks.PUMPKIN_STEM
                || block == Blocks.CARROTS
                || block == Blocks.POTATOES
                || block == Blocks.NETHER_WART
                || block == Blocks.OAK_SAPLING
                || block == Blocks.SPRUCE_SAPLING
                || block == Blocks.BIRCH_SAPLING
                || block == Blocks.JUNGLE_SAPLING
                || block == Blocks.ACACIA_SAPLING
                || block == Blocks.DARK_OAK_SAPLING
                || block == Blocks.MANGROVE_PROPAGULE
                || block == Blocks.CHERRY_SAPLING
                || block == Blocks.SUGAR_CANE
                || block == Blocks.BAMBOO
                || block == Blocks.CACTUS
                || block == Blocks.SWEET_BERRY_BUSH;
    }
}