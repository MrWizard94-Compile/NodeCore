package com.mrwizard94.nodecore.event;

import com.mrwizard94.nodecore.NodeCore;
import com.mrwizard94.nodecore.config.NodeCoreConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;

/**
 * Grants a one-time Base Wars starting kit on a player's first ever join:
 * unbreakable top-tier AIOT, a backpack, and iron armor (all config-driven).
 * Soft on missing mods — an unresolved item id is skipped with a warning.
 */
@Mod.EventBusSubscriber(modid = NodeCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class StartingKitHandler {
    private static final String GRANTED_FLAG = "nodecore_starting_kit_granted";

    private StartingKitHandler() {
    }

    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!NodeCoreConfig.STARTING_KIT_ENABLED.get()) {
            return;
        }
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }

        CompoundTag persisted = player.getPersistentData().getCompound(Player.PERSISTED_NBT_TAG);
        if (persisted.getBoolean(GRANTED_FLAG)) {
            return;
        }

        boolean unbreakable = NodeCoreConfig.STARTING_KIT_UNBREAKABLE.get();

        for (String id : NodeCoreConfig.STARTING_KIT_ITEMS.get()) {
            ItemStack stack = makeStack(id, unbreakable);
            if (!stack.isEmpty()) {
                giveOrDrop(player, stack);
            }
        }

        equipArmor(player, unbreakable);

        persisted.putBoolean(GRANTED_FLAG, true);
        player.getPersistentData().put(Player.PERSISTED_NBT_TAG, persisted);
        NodeCore.LOGGER.info("Granted Base Wars starting kit to {}", player.getGameProfile().getName());
    }

    private static void equipArmor(ServerPlayer player, boolean unbreakable) {
        EquipmentSlot[] slots = {
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET
        };
        List<? extends String> armor = NodeCoreConfig.STARTING_KIT_ARMOR.get();
        for (int i = 0; i < armor.size() && i < slots.length; i++) {
            ItemStack stack = makeStack(armor.get(i), unbreakable);
            if (stack.isEmpty()) {
                continue;
            }
            if (player.getItemBySlot(slots[i]).isEmpty()) {
                player.setItemSlot(slots[i], stack);
            } else {
                giveOrDrop(player, stack);
            }
        }
    }

    private static ItemStack makeStack(String id, boolean unbreakable) {
        ResourceLocation rl = ResourceLocation.tryParse(id);
        if (rl == null) {
            NodeCore.LOGGER.warn("Starting kit: invalid item id '{}'", id);
            return ItemStack.EMPTY;
        }
        Item item = ForgeRegistries.ITEMS.getValue(rl);
        if (item == null || item == Items.AIR) {
            NodeCore.LOGGER.warn("Starting kit: item '{}' not present (mod missing?) — skipping", id);
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(item);
        if (unbreakable && stack.isDamageableItem()) {
            stack.getOrCreateTag().putBoolean("Unbreakable", true);
        }
        return stack;
    }

    private static void giveOrDrop(ServerPlayer player, ItemStack stack) {
        if (!player.getInventory().add(stack)) {
            player.drop(stack, false);
        }
    }
}
