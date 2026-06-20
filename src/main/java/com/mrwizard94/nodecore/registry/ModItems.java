package com.mrwizard94.nodecore.registry;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.item.NodeSurveyItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, NodeCore.MOD_ID);

    public static final RegistryObject<Item> NODE_MARKER = ITEMS.register("node_marker",
            () -> new BlockItem(ModBlocks.NODE_MARKER.get(), new Item.Properties()));

    public static final RegistryObject<Item> NODE_SURVEY = ITEMS.register("node_survey",
            () -> new NodeSurveyItem(new Item.Properties().stacksTo(1)));

    private ModItems() {}
}