package com.mrwizard94.nodecore.registry;

import com.mrwizard94.nodecore.NodeCore;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(ForgeRegistries.FLUIDS, NodeCore.MOD_ID);

    public static ForgeFlowingFluid.Properties LIQUID_FIRE_PROPERTIES;

    public static final RegistryObject<FlowingFluid> LIQUID_FIRE_SOURCE = FLUIDS.register("liquid_fire",
            () -> new ForgeFlowingFluid.Source(LIQUID_FIRE_PROPERTIES));

    public static final RegistryObject<FlowingFluid> LIQUID_FIRE_FLOWING = FLUIDS.register("flowing_liquid_fire",
            () -> new ForgeFlowingFluid.Flowing(LIQUID_FIRE_PROPERTIES));

    static {
        LIQUID_FIRE_PROPERTIES = new ForgeFlowingFluid.Properties(
                ModFluidTypes.LIQUID_FIRE,
                LIQUID_FIRE_SOURCE,
                LIQUID_FIRE_FLOWING)
                .block(() -> ModBlocks.LIQUID_FIRE.get())
                .bucket(() -> ModItems.LIQUID_FIRE_BUCKET.get());
    }

    private ModFluids() {}
}