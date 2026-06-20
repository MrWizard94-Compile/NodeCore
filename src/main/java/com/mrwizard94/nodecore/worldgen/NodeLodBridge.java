package com.mrwizard94.nodecore.worldgen;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.node.NodeType;
import com.mrwizard94.nodecore.node.ResourceNode;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.fml.ModList;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

/**
 * Soft-optional bridge to Large Ore Deposits. No compile-time dependency on any LOD mod;
 * presence is detected at runtime via {@link ModList}.
 */
public final class NodeLodBridge {
    private static final String[] LOD_MOD_IDS = {"lode", "largeoredeposits", "large_ore_deposits"};

    private static final Map<String, NodeType> DEPOSIT_TYPE_MAP = Map.ofEntries(
            Map.entry("iron", NodeType.ORE_IRON),
            Map.entry("iron_deposit", NodeType.ORE_IRON),
            Map.entry("iron_sump", NodeType.ORE_IRON),
            Map.entry("ore_iron", NodeType.ORE_IRON),

            Map.entry("copper", NodeType.ORE_COPPER),
            Map.entry("copper_deposit", NodeType.ORE_COPPER),
            Map.entry("copper_basin", NodeType.ORE_COPPER),
            Map.entry("ore_copper", NodeType.ORE_COPPER),

            Map.entry("brass", NodeType.ORE_BRASS),
            Map.entry("brass_deposit", NodeType.ORE_BRASS),
            Map.entry("brass_basin", NodeType.ORE_BRASS),
            Map.entry("ore_brass", NodeType.ORE_BRASS),

            Map.entry("quartz", NodeType.ORE_QUARTZ),
            Map.entry("nether_quartz", NodeType.ORE_QUARTZ),
            Map.entry("quartz_vein", NodeType.ORE_QUARTZ),
            Map.entry("ore_quartz", NodeType.ORE_QUARTZ),

            Map.entry("lush", NodeType.LUSH_HYDRO),
            Map.entry("lush_hydro", NodeType.LUSH_HYDRO),
            Map.entry("hydro", NodeType.LUSH_HYDRO),
            Map.entry("mega_lush", NodeType.LUSH_HYDRO),

            Map.entry("quartz_rift", NodeType.QUARTZ_RIFT),
            Map.entry("rift", NodeType.QUARTZ_RIFT),
            Map.entry("volcanic_rift", NodeType.QUARTZ_RIFT)
    );

    private NodeLodBridge() {}

    public static boolean isLodPresent() {
        for (String modId : LOD_MOD_IDS) {
            if (ModList.get().isLoaded(modId)) {
                return true;
            }
        }
        return false;
    }

    public static Optional<NodeType> mapDepositType(String depositType) {
        if (depositType == null || depositType.isBlank()) {
            return Optional.empty();
        }

        String normalized = normalizeDepositKey(depositType);
        NodeType mapped = DEPOSIT_TYPE_MAP.get(normalized);
        if (mapped != null) {
            return Optional.of(mapped);
        }

        try {
            return Optional.of(NodeType.byId(normalized));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    public static boolean registerDeposit(ServerLevel level, BlockPos center, String depositType) {
        Optional<NodeType> type = mapDepositType(depositType);
        if (type.isEmpty()) {
            NodeCore.LOGGER.warn("Skipping LOD deposit at {} — unknown deposit type '{}'", center, depositType);
            return false;
        }

        NodeSavedData data = NodeSavedData.get(level);
        if (data.findByCenter(center).isPresent()) {
            NodeCore.LOGGER.debug("LOD deposit at {} already linked to a node", center);
            return false;
        }

        if (!NodeWorldgenStub.shouldPlaceNode(level, center, type.get())) {
            NodeCore.LOGGER.debug("LOD deposit at {} rejected — violates min node spacing", center);
            return false;
        }

        int radius = type.get().getDefaultRadius();
        ResourceNode node = data.addNode(type.get(), center, radius);
        NodeCore.LOGGER.info("Linked LOD deposit '{}' at {} as {} node [{}]",
                depositType, center, type.get().getId(), node.getId());
        return true;
    }

    private static String normalizeDepositKey(String raw) {
        String key = raw.trim().toLowerCase(Locale.ROOT);
        int colon = key.lastIndexOf(':');
        if (colon >= 0 && colon < key.length() - 1) {
            key = key.substring(colon + 1);
        }
        return key;
    }
}