package com.mrwizard94.nodecore.block.fluid;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

public class LiquidFireBlock extends LiquidBlock {
    public LiquidFireBlock(Supplier<? extends FlowingFluid> fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        int chance = NodeCoreConfig.LIQUID_FIRE_PARTICLE_CHANCE.get();
        if (chance <= 0 || random.nextInt(chance) != 0) {
            return;
        }

        double x = pos.getX() + random.nextDouble();
        double y = pos.getY() + random.nextDouble() * 0.85D + 0.1D;
        double z = pos.getZ() + random.nextDouble();
        level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x, y, z, 0.0D, 0.02D, 0.0D);
    }
}