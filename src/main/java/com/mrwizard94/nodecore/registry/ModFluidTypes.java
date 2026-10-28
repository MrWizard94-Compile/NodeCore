package com.mrwizard94.nodecore.registry;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.common.SoundActions;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Consumer;

public final class ModFluidTypes {
    private static final ResourceLocation LIQUID_FIRE_STILL_TEXTURE =
            new ResourceLocation(NodeCore.MOD_ID, "block/liquid_fire_still");
    private static final ResourceLocation LIQUID_FIRE_FLOWING_TEXTURE =
            new ResourceLocation(NodeCore.MOD_ID, "block/liquid_fire_flow");

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, NodeCore.MOD_ID);

    public static final RegistryObject<FluidType> LIQUID_FIRE = FLUID_TYPES.register("liquid_fire",
            () -> new FluidType(FluidType.Properties.create()
                    .canSwim(false)
                    .canDrown(false)
                    .canPushEntity(false)
                    .fallDistanceModifier(0.0F)
                    .pathType(BlockPathTypes.LAVA)
                    .adjacentPathType(null)
                    .density(1200)
                    .viscosity(1500)
                    .temperature(2000)
                    .lightLevel(11)
                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)
                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)) {
                @Override
                public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
                    consumer.accept(new IClientFluidTypeExtensions() {
                        @Override
                        public ResourceLocation getStillTexture() {
                            return LIQUID_FIRE_STILL_TEXTURE;
                        }

                        @Override
                        public ResourceLocation getFlowingTexture() {
                            return LIQUID_FIRE_FLOWING_TEXTURE;
                        }

                        @Override
                        public int getTintColor() {
                            return NodeCoreConfig.liquidFireTintArgb();
                        }
                    });
                }
            });

    private ModFluidTypes() {}
}