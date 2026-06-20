package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.node.NodeQueries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SaplingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import net.minecraft.util.RandomSource;

public class LushGrowthHandler {

    @SubscribeEvent
    public void onLevelTick(TickEvent.LevelTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (!NodeCoreConfig.LUSH_GROWTH_ENABLED.get()) {
            return;
        }
        if (!(event.level instanceof ServerLevel serverLevel)) {
            return;
        }

        int interval = NodeCoreConfig.LUSH_GROWTH_INTERVAL_TICKS.get();
        if (serverLevel.getGameTime() % interval != 0) {
            return;
        }

        if (serverLevel.players().isEmpty()) {
            return;
        }

        for (int i = 0; i < 4; i++) {
            BlockPos sample = randomLoadedPos(serverLevel);
            if (sample == null || !NodeQueries.isInLushNode(serverLevel, sample)) {
                continue;
            }
            accelerateGrowth(serverLevel, sample);
        }
    }

    private static BlockPos randomLoadedPos(ServerLevel level) {
        RandomSource random = level.getRandom();
        var player = level.players().get(random.nextInt(level.players().size()));
        int x = player.blockPosition().getX() + random.nextInt(32) - 16;
        int z = player.blockPosition().getZ() + random.nextInt(32) - 16;
        int y = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, x, z);
        return new BlockPos(x, y, z);
    }

    private static void accelerateGrowth(ServerLevel level, BlockPos origin) {
        for (BlockPos pos : BlockPos.betweenClosed(origin.offset(-2, -1, -2), origin.offset(2, 2, 2))) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();
            if (block instanceof CropBlock crop && !crop.isMaxAge(state)) {
                level.setBlock(pos, crop.getStateForAge(crop.getAge(state) + 1), 2);
            } else if (block instanceof SaplingBlock sapling) {
                sapling.advanceTree(level, pos, state, level.getRandom());
            }
        }
    }
}