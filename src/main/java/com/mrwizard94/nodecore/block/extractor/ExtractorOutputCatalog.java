package com.mrwizard94.nodecore.block.extractor;

import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.node.NodeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public final class ExtractorOutputCatalog {
    private ExtractorOutputCatalog() {}

    public static ItemStack rollOutput(NodeType nodeType, RandomSource random) {
        if (random.nextDouble() < NodeCoreConfig.EXTRACTOR_RAW_ORE_BLOCK_CHANCE.get()) {
            return new ItemStack(resolveOreBlock(nodeType));
        }
        return new ItemStack(resolveProcessableOre(nodeType));
    }

    private static Item resolveProcessableOre(NodeType nodeType) {
        return switch (nodeType) {
            case ORE_IRON -> Items.RAW_IRON;
            case ORE_COPPER -> Items.RAW_COPPER;
            case ORE_BRASS -> resolveOptionalItem("create:raw_zinc", Items.RAW_COPPER);
            case ORE_QUARTZ, QUARTZ_RIFT -> Items.QUARTZ;
            default -> Items.RAW_IRON;
        };
    }

    private static Block resolveOreBlock(NodeType nodeType) {
        return switch (nodeType) {
            case ORE_IRON -> Blocks.IRON_ORE;
            case ORE_COPPER -> Blocks.COPPER_ORE;
            case ORE_BRASS -> resolveOptionalBlock("create:zinc_ore", Blocks.COPPER_ORE);
            case ORE_QUARTZ, QUARTZ_RIFT -> Blocks.NETHER_QUARTZ_ORE;
            default -> Blocks.IRON_ORE;
        };
    }

    private static Item resolveOptionalItem(String id, Item fallback) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            return fallback;
        }
        Item item = ForgeRegistries.ITEMS.getValue(location);
        return item == null || item == Items.AIR ? fallback : item;
    }

    private static Block resolveOptionalBlock(String id, Block fallback) {
        ResourceLocation location = ResourceLocation.tryParse(id);
        if (location == null) {
            return fallback;
        }
        Block block = ForgeRegistries.BLOCKS.getValue(location);
        return block == null || block == Blocks.AIR ? fallback : block;
    }
}