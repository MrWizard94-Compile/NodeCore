package com.mrwizard94.nodecore.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.node.NodeType;
import com.mrwizard94.nodecore.node.ResourceNode;
import com.mrwizard94.nodecore.integration.NodeInControlExporter;
import com.mrwizard94.nodecore.worldgen.NodeLodBridge;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.Comparator;
import java.util.Optional;

public final class NodeCoreCommands {
    private NodeCoreCommands() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("nodecore")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("add")
                        .then(Commands.argument("type", NodeTypeArgument.nodeType())
                                .executes(ctx -> addNode(ctx, null, null))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> addNode(ctx,
                                                BlockPosArgument.getLoadedBlockPos(ctx, "pos"), null))
                                        .then(Commands.argument("radius", IntegerArgumentType.integer(8, 256))
                                                .executes(ctx -> addNode(ctx,
                                                        BlockPosArgument.getLoadedBlockPos(ctx, "pos"),
                                                        IntegerArgumentType.getInteger(ctx, "radius")))))))
                .then(Commands.literal("list")
                        .executes(NodeCoreCommands::listNodes))
                .then(Commands.literal("nearest")
                        .executes(ctx -> nearestNode(ctx, null))
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                .executes(ctx -> nearestNode(ctx,
                                        BlockPosArgument.getLoadedBlockPos(ctx, "pos")))))
                .then(Commands.literal("remove")
                        .then(Commands.argument("id", StringArgumentType.string())
                                .executes(NodeCoreCommands::removeNode)))
                .then(Commands.literal("link")
                        .then(Commands.argument("type", NodeTypeArgument.nodeType())
                                .executes(ctx -> linkDeposit(ctx, null))
                                .then(Commands.argument("pos", BlockPosArgument.blockPos())
                                        .executes(ctx -> linkDeposit(ctx,
                                                BlockPosArgument.getLoadedBlockPos(ctx, "pos"))))))
                .then(Commands.literal("export")
                        .then(Commands.literal("incontrol")
                                .executes(NodeCoreCommands::exportInControl))));
    }

    private static int addNode(CommandContext<CommandSourceStack> ctx, BlockPos pos, Integer radius) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        NodeType type = NodeTypeArgument.getNodeType(ctx, "type");

        BlockPos center = pos;
        if (center == null) {
            if (!(source.getEntity() instanceof ServerPlayer player)) {
                source.sendFailure(Component.literal("Position required when not run by a player."));
                return 0;
            }
            center = player.blockPosition();
        }

        int nodeRadius = radius != null ? radius
                : (type.getDefaultRadius() > 0 ? type.getDefaultRadius() : NodeCoreConfig.DEFAULT_NODE_RADIUS.get());

        NodeSavedData data = NodeSavedData.get(level);
        ResourceNode node = data.addNode(type, center, nodeRadius);
        final BlockPos registeredCenter = center;
        final int registeredRadius = nodeRadius;

        source.sendSuccess(() -> Component.translatable("nodecore.command.added",
                type.getDisplayName(),
                node.getId().toString().substring(0, 8),
                registeredCenter.getX(), registeredCenter.getY(), registeredCenter.getZ(),
                registeredRadius).withStyle(ChatFormatting.GREEN), true);
        return 1;
    }

    private static int listNodes(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        var nodes = NodeSavedData.get(source.getLevel()).getNodes();

        if (nodes.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("nodecore.command.list.empty")
                    .withStyle(ChatFormatting.GRAY), false);
            return 1;
        }

        source.sendSuccess(() -> Component.translatable("nodecore.command.list.header", nodes.size())
                .withStyle(ChatFormatting.GOLD), false);

        nodes.stream()
                .sorted(Comparator.comparing(n -> n.getType().getId()))
                .forEach(node -> source.sendSuccess(() -> Component.literal(String.format(
                        "  [%s] %s @ (%d, %d, %d) r=%d",
                        node.getId().toString().substring(0, 8),
                        node.getType().getId(),
                        node.getCenter().getX(),
                        node.getCenter().getY(),
                        node.getCenter().getZ(),
                        node.getRadius())).withStyle(ChatFormatting.AQUA), false));

        return nodes.size();
    }

    private static int nearestNode(CommandContext<CommandSourceStack> ctx, BlockPos pos) {
        CommandSourceStack source = ctx.getSource();
        BlockPos query = pos;

        if (query == null) {
            if (!(source.getEntity() instanceof ServerPlayer player)) {
                source.sendFailure(Component.literal("Position required when not run by a player."));
                return 0;
            }
            query = player.blockPosition();
        }

        Optional<ResourceNode> nearest = NodeSavedData.get(source.getLevel()).findNearest(query, null);

        if (nearest.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("nodecore.command.nearest.none")
                    .withStyle(ChatFormatting.GRAY), false);
            return 0;
        }

        ResourceNode node = nearest.get();
        int distance = (int) node.distanceTo(query);
        source.sendSuccess(() -> Component.translatable("nodecore.command.nearest.found",
                node.getType().getDisplayName(),
                node.getCenter().getX(), node.getCenter().getY(), node.getCenter().getZ(),
                distance,
                node.getRadius(),
                node.getId().toString().substring(0, 8)).withStyle(ChatFormatting.AQUA), false);
        return 1;
    }

    private static int linkDeposit(CommandContext<CommandSourceStack> ctx, BlockPos pos) {
        CommandSourceStack source = ctx.getSource();
        ServerLevel level = source.getLevel();
        NodeType type = NodeTypeArgument.getNodeType(ctx, "type");

        BlockPos center = pos;
        if (center == null) {
            if (!(source.getEntity() instanceof ServerPlayer player)) {
                source.sendFailure(Component.literal("Position required when not run by a player."));
                return 0;
            }
            center = player.blockPosition();
        }

        final BlockPos linkCenter = center;

        if (NodeLodBridge.registerDeposit(level, linkCenter, type.getId())) {
            source.sendSuccess(() -> Component.translatable("nodecore.command.linked",
                    type.getDisplayName(),
                    linkCenter.getX(), linkCenter.getY(), linkCenter.getZ()).withStyle(ChatFormatting.GREEN), true);
            return 1;
        }

        if (NodeSavedData.get(level).findByCenter(linkCenter).isPresent()) {
            source.sendFailure(Component.translatable("nodecore.command.link.already_linked", linkCenter.getX(),
                    linkCenter.getY(), linkCenter.getZ()));
            return 0;
        }

        source.sendFailure(Component.translatable("nodecore.command.link.rejected",
                type.getDisplayName(),
                linkCenter.getX(), linkCenter.getY(), linkCenter.getZ()));
        return 0;
    }

    private static int exportInControl(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();

        if (!NodeCoreConfig.SPAWN_EXPORT_ENABLED.get()) {
            source.sendFailure(Component.translatable("nodecore.command.export.incontrol.disabled"));
            return 0;
        }

        try {
            NodeInControlExporter.ExportResult result = NodeInControlExporter.export(source.getLevel());
            source.sendSuccess(() -> Component.translatable("nodecore.command.export.incontrol.success",
                    result.nodeCount(),
                    result.areasPath().toString(),
                    result.spawnerPath().toString()).withStyle(ChatFormatting.GREEN), true);
            return result.nodeCount();
        } catch (IllegalStateException e) {
            source.sendFailure(Component.translatable("nodecore.command.export.incontrol.disabled"));
            return 0;
        } catch (Exception e) {
            source.sendFailure(Component.translatable("nodecore.command.export.incontrol.failed", e.getMessage()));
            return 0;
        }
    }

    private static int removeNode(CommandContext<CommandSourceStack> ctx) {
        CommandSourceStack source = ctx.getSource();
        String idPrefix = StringArgumentType.getString(ctx, "id");
        NodeSavedData data = NodeSavedData.get(source.getLevel());

        Optional<ResourceNode> match = data.getNodes().stream()
                .filter(n -> n.getId().toString().startsWith(idPrefix))
                .findFirst();

        if (match.isEmpty()) {
            source.sendFailure(Component.translatable("nodecore.command.remove.not_found", idPrefix));
            return 0;
        }

        ResourceNode removed = match.get();
        data.removeNode(removed.getId());

        source.sendSuccess(() -> Component.translatable("nodecore.command.removed",
                removed.getType().getDisplayName(),
                removed.getId().toString().substring(0, 8)).withStyle(ChatFormatting.YELLOW), true);
        return 1;
    }
}