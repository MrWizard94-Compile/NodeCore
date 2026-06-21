package com.mrwizard94.nodecore.block.extractor;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.registry.ModItems;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

public enum ExtractorFuelTier {
    NONE,
    CHARCOAL,
    COAL,
    LAVA,
    LIQUID_FIRE;

    public double boostMultiplier() {
        return switch (this) {
            case CHARCOAL -> NodeCoreConfig.EXTRACTOR_FUEL_BOOST_CHARCOAL.get();
            case COAL -> NodeCoreConfig.EXTRACTOR_FUEL_BOOST_COAL.get();
            case LAVA -> NodeCoreConfig.EXTRACTOR_FUEL_BOOST_LAVA.get();
            case LIQUID_FIRE -> NodeCoreConfig.EXTRACTOR_FUEL_BOOST_LIQUID_FIRE.get();
            case NONE -> 1.0D;
        };
    }

    public static ExtractorFuelTier fromSolidItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return NONE;
        }
        if (stack.is(Items.CHARCOAL)) {
            return CHARCOAL;
        }
        if (stack.is(Items.COAL)) {
            return COAL;
        }
        if (stack.is(Items.LAVA_BUCKET)) {
            return LAVA;
        }
        if (stack.is(ModItems.LIQUID_FIRE_BUCKET.get())) {
            return LIQUID_FIRE;
        }
        return NONE;
    }

    public static ExtractorFuelTier resolveActive(IItemHandler fuelSlot, int solidBurnTicks, FluidStack lava) {
        if (lava != null && !lava.isEmpty() && lava.getFluid() == Fluids.LAVA
                && lava.getAmount() >= NodeCoreConfig.EXTRACTOR_LAVA_CONSUMPTION_MB.get()) {
            return LAVA;
        }

        ItemStack fuel = fuelSlot.getStackInSlot(0);
        if (solidBurnTicks > 0) {
            ExtractorFuelTier tier = fromSolidItem(fuel);
            return tier == NONE ? CHARCOAL : tier;
        }

        return NONE;
    }
}