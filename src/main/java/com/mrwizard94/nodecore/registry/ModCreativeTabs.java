package com.mrwizard94.nodecore.registry;

import com.mrwizard94.nodecore.NodeCore;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, NodeCore.MOD_ID);

    public static final RegistryObject<CreativeModeTab> NODE_CORE_TAB = CREATIVE_TABS.register("node_core",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.nodecore"))
                    .icon(() -> new ItemStack(ModItems.NODE_SURVEY.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModItems.NODE_MARKER.get());
                        output.accept(ModItems.NODE_SURVEY.get());
                    })
                    .build());

    private ModCreativeTabs() {}
}