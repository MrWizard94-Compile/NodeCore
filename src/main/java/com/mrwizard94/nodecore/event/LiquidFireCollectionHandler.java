package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.data.LiquidFireCollectionData;
import com.mrwizard94.nodecore.registry.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Map;

@Mod.EventBusSubscriber(modid = NodeCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class LiquidFireCollectionHandler {
    private LiquidFireCollectionHandler() {}

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level) || event.isCanceled()) {
            return;
        }
        if (!NodeCoreConfig.LIQUID_FIRE_COLLECTION_ENABLED.get()) {
            return;
        }

        BlockPos placedPos = event.getPos();
        BlockState placed = event.getPlacedBlock();
        if (placed.is(Blocks.LAVA) || placed.is(Blocks.SOUL_SAND) || placed.is(Blocks.SOUL_SOIL)) {
            tryRegisterNear(placedPos, level);
        }
        if (placed.is(Blocks.CAULDRON)) {
            tryRegisterCollector(level, placedPos);
        }
    }

    @SubscribeEvent
    public static void onNeighborNotify(BlockEvent.NeighborNotifyEvent event) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }
        if (!NodeCoreConfig.LIQUID_FIRE_COLLECTION_ENABLED.get()) {
            return;
        }
        tryRegisterNear(event.getPos(), level);
    }

    @SubscribeEvent
    public static void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.level instanceof ServerLevel level)) {
            return;
        }
        if (!NodeCoreConfig.LIQUID_FIRE_COLLECTION_ENABLED.get()) {
            return;
        }

        int interval = NodeCoreConfig.LIQUID_FIRE_COLLECTION_INTERVAL_TICKS.get();
        if (interval <= 0 || level.getGameTime() % interval != 0) {
            return;
        }

        LiquidFireCollectionData data = LiquidFireCollectionData.get(level);
        int fillTicks = NodeCoreConfig.LIQUID_FIRE_COLLECTION_FILL_TICKS.get();

        for (Map.Entry<BlockPos, LiquidFireCollectionData.CollectorState> entry : data.snapshot().entrySet()) {
            BlockPos cauldronPos = entry.getKey();
            LiquidFireCollectionData.CollectorState state = entry.getValue();

            if (!isCollectorStructureValid(level, cauldronPos)) {
                data.untrack(cauldronPos);
                continue;
            }

            if (state.ready) {
                spawnCollectionParticles(level, cauldronPos, 3);
                continue;
            }

            int nextProgress = state.progressTicks + interval;
            boolean ready = nextProgress >= fillTicks;
            if (ready) {
                nextProgress = fillTicks;
            }
            data.setProgress(cauldronPos, nextProgress, ready);
            updateCauldronVisual(level, cauldronPos, nextProgress, fillTicks);
            spawnCollectionParticles(level, cauldronPos, ready ? 3 : 1);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onCauldronInteract(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide() || event.isCanceled()) {
            return;
        }
        if (!NodeCoreConfig.LIQUID_FIRE_COLLECTION_ENABLED.get()) {
            return;
        }

        ServerLevel level = (ServerLevel) event.getLevel();
        BlockPos pos = event.getPos();
        LiquidFireCollectionData data = LiquidFireCollectionData.get(level);
        if (!data.isTracked(pos)) {
            return;
        }

        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }

        if (held.is(Items.BUCKET) && data.isReady(pos)) {
            if (!player.addItem(new ItemStack(ModItems.LIQUID_FIRE_BUCKET.get()))) {
                player.drop(new ItemStack(ModItems.LIQUID_FIRE_BUCKET.get()), false);
            }
            held.shrink(1);
            if (!player.getAbilities().instabuild) {
                player.addItem(new ItemStack(Items.BUCKET));
            }
            data.reset(pos);
            level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
            event.setCanceled(true);
            event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
            return;
        }

        if (data.isTracked(pos) && !data.isReady(pos)) {
            event.setCanceled(true);
            event.setCancellationResult(net.minecraft.world.InteractionResult.FAIL);
        }
    }

    private static void tryRegisterNear(BlockPos origin, ServerLevel level) {
        for (BlockPos candidate : BlockPos.betweenClosed(origin.offset(-2, -2, -2), origin.offset(2, 2, 2))) {
            if (level.getBlockState(candidate).is(Blocks.CAULDRON)
                    || level.getBlockState(candidate).is(Blocks.WATER_CAULDRON)) {
                tryRegisterCollector(level, candidate);
            }
        }
    }

    private static void tryRegisterCollector(ServerLevel level, BlockPos cauldronPos) {
        if (isCollectorStructureValid(level, cauldronPos)) {
            LiquidFireCollectionData.get(level).track(cauldronPos);
        }
    }

    private static boolean isCollectorStructureValid(Level level, BlockPos cauldronPos) {
        BlockState cauldron = level.getBlockState(cauldronPos);
        if (!cauldron.is(Blocks.CAULDRON) && !cauldron.is(Blocks.WATER_CAULDRON)) {
            return false;
        }

        BlockPos soulPos = cauldronPos.above();
        BlockState soul = level.getBlockState(soulPos);
        if (!soul.is(Blocks.SOUL_SAND) && !soul.is(Blocks.SOUL_SOIL)) {
            return false;
        }

        BlockState lava = level.getBlockState(soulPos.above());
        return lava.is(Blocks.LAVA);
    }

    private static void updateCauldronVisual(ServerLevel level, BlockPos pos, int progress, int required) {
        if (required <= 0) {
            return;
        }

        float ratio = progress / (float) required;
        if (ratio <= 0.0F) {
            if (!level.getBlockState(pos).is(Blocks.CAULDRON)) {
                level.setBlock(pos, Blocks.CAULDRON.defaultBlockState(), 3);
            }
            return;
        }

        int cauldronLevel = ratio < 0.34F ? 1 : (ratio < 0.67F ? 2 : 3);
        BlockState visual = Blocks.WATER_CAULDRON.defaultBlockState()
                .setValue(LayeredCauldronBlock.LEVEL, cauldronLevel);
        if (!level.getBlockState(pos).equals(visual)) {
            level.setBlock(pos, visual, 3);
        }
    }

    private static void spawnCollectionParticles(ServerLevel level, BlockPos pos, int count) {
        for (int i = 0; i < count; i++) {
            double x = pos.getX() + 0.25D + level.random.nextDouble() * 0.5D;
            double y = pos.getY() + 0.55D + level.random.nextDouble() * 0.25D;
            double z = pos.getZ() + 0.25D + level.random.nextDouble() * 0.5D;
            level.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 1, 0.0D, 0.03D, 0.0D, 0.01D);
        }
    }
}