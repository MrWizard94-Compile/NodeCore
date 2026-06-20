package com.mrwizard94.nodecore.node;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

public enum NodeType implements StringRepresentable {
    ORE_IRON("ore_iron", NodeCategory.ORE, 128),
    ORE_COPPER("ore_copper", NodeCategory.ORE, 112),
    ORE_BRASS("ore_brass", NodeCategory.ORE, 120),
    ORE_QUARTZ("ore_quartz", NodeCategory.ORE, 96),
    LUSH_HYDRO("lush_hydro", NodeCategory.LUSH, 160),
    QUARTZ_RIFT("quartz_rift", NodeCategory.ORE, 144);

    public enum NodeCategory {
        ORE,
        LUSH,
        MISC
    }

    private final String id;
    private final NodeCategory category;
    private final int defaultRadius;

    NodeType(String id, NodeCategory category, int defaultRadius) {
        this.id = id;
        this.category = category;
        this.defaultRadius = defaultRadius;
    }

    public String getId() {
        return id;
    }

    public NodeCategory getCategory() {
        return category;
    }

    public int getDefaultRadius() {
        return defaultRadius;
    }

    public boolean isOre() {
        return category == NodeCategory.ORE;
    }

    public boolean isLush() {
        return category == NodeCategory.LUSH;
    }

    public Component getDisplayName() {
        return Component.translatable("nodecore.node_type." + id);
    }

    public static NodeType byId(String raw) {
        for (NodeType type : values()) {
            if (type.id.equalsIgnoreCase(raw)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown node type: " + raw);
    }

    @Override
    public String getSerializedName() {
        return id;
    }
}