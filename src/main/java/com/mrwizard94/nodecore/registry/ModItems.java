package com.mrwizard94.nodecore.registry;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.item.NodeSurveyItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
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

    public static final RegistryObject<Item> LIQUID_FIRE_BUCKET = ITEMS.register("liquid_fire_bucket",
            () -> new BucketItem(ModFluids.LIQUID_FIRE_SOURCE,
                    new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

    public static final RegistryObject<Item> ORE_EXTRACTOR_CORE = ITEMS.register("ore_extractor_core",
            () -> new BlockItem(ModBlocks.ORE_EXTRACTOR_CORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_EXTRACTOR_FRAME = ITEMS.register("ore_extractor_frame",
            () -> new BlockItem(ModBlocks.ORE_EXTRACTOR_FRAME.get(), new Item.Properties()));

    public static final RegistryObject<Item> ASTRAL_NEXUS_CRYSTAL_BLOCK = ITEMS.register("astral_nexus_crystal_block",
            () -> new BlockItem(ModBlocks.ASTRAL_NEXUS_CRYSTAL_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> ARCANE_ORE_BLOCK = ITEMS.register("arcane_ore_block",
            () -> new BlockItem(ModBlocks.ARCANE_ORE_BLOCK.get(), new Item.Properties()));

    private ModItems() {}
}