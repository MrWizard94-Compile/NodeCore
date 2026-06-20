package com.mrwizard94.nodecore.item;

import com.mrwizard94.nodecore.data.NodeSavedData;
import com.mrwizard94.nodecore.node.ResourceNode;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class NodeSurveyItem extends Item {
    public NodeSurveyItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide || !(level instanceof ServerLevel serverLevel)) {
            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        NodeSavedData data = NodeSavedData.get(serverLevel);
        Optional<ResourceNode> nearest = data.findNearest(player.blockPosition(), null);

        if (nearest.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("nodecore.survey.none").withStyle(ChatFormatting.GRAY),
                    true);
            return InteractionResultHolder.success(stack);
        }

        ResourceNode node = nearest.get();
        int distance = (int) node.distanceTo(player.blockPosition());
        player.displayClientMessage(
                Component.translatable("nodecore.survey.nearest",
                        node.getType().getDisplayName(),
                        node.getCenter().toShortString(),
                        distance,
                        node.getRadius()).withStyle(ChatFormatting.AQUA),
                true);

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.nodecore.node_survey").withStyle(ChatFormatting.DARK_GRAY));
    }
}