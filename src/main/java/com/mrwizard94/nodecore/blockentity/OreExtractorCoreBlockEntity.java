package com.mrwizard94.nodecore.blockentity;

import com.mrwizard94.nodecore.block.extractor.ExtractorFuelTier;
import com.mrwizard94.nodecore.block.extractor.ExtractorMultiblock;
import com.mrwizard94.nodecore.block.extractor.ExtractorOutputCatalog;
import com.mrwizard94.nodecore.block.extractor.ExtractorRegistry;
import com.mrwizard94.nodecore.config.NodeCoreConfig;

import com.mrwizard94.nodecore.node.NodeQueries;
import com.mrwizard94.nodecore.node.NodeType;
import com.mrwizard94.nodecore.node.ResourceNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import java.util.Optional;
import java.util.UUID;

public class OreExtractorCoreBlockEntity extends BlockEntity {
    private final ItemStackHandler buffer = new ItemStackHandler(NodeCoreConfig.EXTRACTOR_BUFFER_SLOTS.get()) {
        @Override
        public int getSlotLimit(int slot) {
            return NodeCoreConfig.EXTRACTOR_BUFFER_STACK_LIMIT.get();
        }
    };

    private final ItemStackHandler fuelSlot = new ItemStackHandler(1) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return ExtractorFuelTier.fromSolidItem(stack) != ExtractorFuelTier.NONE;
        }
    };

    private final FluidTank lavaTank = new FluidTank(NodeCoreConfig.EXTRACTOR_LAVA_TANK_CAPACITY.get(), fluid ->
            fluid.getFluid().isSame(net.minecraft.world.level.material.Fluids.LAVA));

    private final LazyOptional<IItemHandler> externalItemCapability =
            LazyOptional.of(() -> new ExtractorMultiblockItemHandler(fuelSlot, buffer));
    private final LazyOptional<IFluidHandler> fluidCapability = LazyOptional.of(() -> lavaTank);

    private UUID linkedNodeId;
    private BlockPos linkedNodePos = BlockPos.ZERO;
    private NodeType linkedNodeType = NodeType.ORE_IRON;
    private int linkedTier = 1;

    private double productionAccumulator;
    private int solidFuelBurnTicks;
    private ExtractorFuelTier activeFuelTier = ExtractorFuelTier.NONE;
    private int structureRecheckCooldown;
    private int decayCooldown;
    private boolean structureValid;
    private boolean powered;

    public OreExtractorCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.ORE_EXTRACTOR_CORE.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, OreExtractorCoreBlockEntity entity) {
        if (level.isClientSide() || !NodeCoreConfig.EXTRACTOR_ENABLED.get()) {
            return;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        entity.tick(serverLevel);
    }

    private void tick(ServerLevel level) {
        if (structureRecheckCooldown-- <= 0) {
            structureRecheckCooldown = NodeCoreConfig.EXTRACTOR_STRUCTURE_CHECK_INTERVAL.get();
            structureValid = refreshStructure(level);
        }

        powered = ExtractorMultiblock.findLayout(level, worldPosition)
                .map(layout -> ExtractorMultiblock.maxAssemblyKineticSpeed(level, layout)
                        >= NodeCoreConfig.EXTRACTOR_MIN_ROTATION_SPEED.get())
                .orElse(false);

        activeFuelTier = ExtractorFuelTier.resolveActive(fuelSlot, solidFuelBurnTicks, lavaTank.getFluid());
        consumeFuel();

        if (!structureValid || linkedNodeId == null) {
            handleDecay();
            setChanged();
            return;
        }

        if (!powered) {
            handleDecay();
            setChanged();
            return;
        }

        if (isBufferFull()) {
            setChanged();
            return;
        }

        double rate = NodeCoreConfig.EXTRACTOR_BASE_PRODUCTION_PER_TICK.get()
                * tierMultiplier()
                * activeFuelTier.boostMultiplier();
        productionAccumulator += rate;

        while (productionAccumulator >= 1.0D) {
            if (isBufferFull()) {
                break;
            }
            ItemStack output = ExtractorOutputCatalog.rollOutput(linkedNodeType, level.getRandom());
            if (!insertIntoBuffer(output)) {
                break;
            }
            productionAccumulator -= 1.0D;
        }

        setChanged();
    }

    private void handleDecay() {
        if (!bufferHasItems()) {
            return;
        }
        if (decayCooldown-- > 0) {
            return;
        }
        decayCooldown = NodeCoreConfig.EXTRACTOR_DECAY_INTERVAL_TICKS.get();

        int amount = NodeCoreConfig.EXTRACTOR_DECAY_AMOUNT.get();
        for (int slot = 0; slot < buffer.getSlots() && amount > 0; slot++) {
            ItemStack stack = buffer.getStackInSlot(slot);
            if (stack.isEmpty()) {
                continue;
            }
            int removed = Math.min(amount, stack.getCount());
            stack.shrink(removed);
            amount -= removed;
            buffer.setStackInSlot(slot, stack);
        }
    }

    private void consumeFuel() {
        if (activeFuelTier == ExtractorFuelTier.LAVA && lavaTank.getFluidAmount() > 0) {
            lavaTank.drain(NodeCoreConfig.EXTRACTOR_LAVA_CONSUMPTION_MB.get(), IFluidHandler.FluidAction.EXECUTE);
            return;
        }

        if (solidFuelBurnTicks > 0) {
            solidFuelBurnTicks--;
            return;
        }

        ItemStack fuel = fuelSlot.getStackInSlot(0);
        if (fuel.isEmpty()) {
            return;
        }

        ExtractorFuelTier tier = ExtractorFuelTier.fromSolidItem(fuel);
        if (tier == ExtractorFuelTier.NONE) {
            return;
        }

        int burnTicks = switch (tier) {
            case CHARCOAL -> NodeCoreConfig.EXTRACTOR_FUEL_BURN_CHARCOAL.get();
            case COAL -> NodeCoreConfig.EXTRACTOR_FUEL_BURN_COAL.get();
            case LAVA -> NodeCoreConfig.EXTRACTOR_FUEL_BURN_LAVA.get();
            case LIQUID_FIRE -> NodeCoreConfig.EXTRACTOR_FUEL_BURN_LIQUID_FIRE.get();
            case NONE -> 0;
        };

        if (burnTicks <= 0) {
            return;
        }

        solidFuelBurnTicks = burnTicks;
        if (fuel.hasCraftingRemainingItem()) {
            fuelSlot.setStackInSlot(0, fuel.getCraftingRemainingItem());
        } else {
            fuel.shrink(1);
            fuelSlot.setStackInSlot(0, fuel);
        }
    }

    public void requestStructureRecheck() {
        structureRecheckCooldown = 0;
    }

    private boolean refreshStructure(ServerLevel level) {
        Optional<ExtractorMultiblock.Layout> layout = ExtractorMultiblock.findLayout(level, worldPosition);
        if (layout.isEmpty()) {
            return false;
        }

        ResourceNode resourceNode = layout.get().node();
        if (linkedNodeId == null || !linkedNodeId.equals(resourceNode.getId())) {
            if (!ExtractorRegistry.get(level).register(resourceNode.getId(), worldPosition)) {
                return false;
            }
            linkedNodeId = resourceNode.getId();
        }

        linkedNodePos = resourceNode.getCenter();
        linkedNodeType = resourceNode.getType();
        linkedTier = NodeQueries.tierAt(level, linkedNodePos);
        return linkedTier > 0;
    }

    public boolean tryClaimNode(ServerLevel level) {
        Optional<ExtractorMultiblock.Layout> layout = ExtractorMultiblock.findLayout(level, worldPosition);
        if (layout.isEmpty()) {
            return false;
        }

        ResourceNode resourceNode = layout.get().node();
        Optional<BlockPos> existing = ExtractorRegistry.get(level).findExtractorForNode(resourceNode.getId());
        if (existing.isPresent() && !existing.get().equals(worldPosition)) {
            return false;
        }

        return ExtractorRegistry.get(level).register(resourceNode.getId(), worldPosition);
    }

    public void releaseClaim(ServerLevel level) {
        ExtractorRegistry.get(level).unregister(worldPosition);
        linkedNodeId = null;
        linkedNodePos = BlockPos.ZERO;
        linkedTier = 1;
        structureValid = false;
        setChanged();
    }

    private double tierMultiplier() {
        return switch (linkedTier) {
            case 1 -> NodeCoreConfig.EXTRACTOR_TIER_1_MULTIPLIER.get();
            case 2 -> NodeCoreConfig.EXTRACTOR_TIER_2_MULTIPLIER.get();
            case 3 -> NodeCoreConfig.EXTRACTOR_TIER_3_MULTIPLIER.get();
            default -> 1.0D;
        };
    }

    private boolean isBufferFull() {
        for (int slot = 0; slot < buffer.getSlots(); slot++) {
            ItemStack stack = buffer.getStackInSlot(slot);
            if (stack.isEmpty() || stack.getCount() < buffer.getSlotLimit(slot)) {
                return false;
            }
        }
        return true;
    }

    private boolean bufferHasItems() {
        for (int slot = 0; slot < buffer.getSlots(); slot++) {
            if (!buffer.getStackInSlot(slot).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean insertIntoBuffer(ItemStack stack) {
        for (int slot = 0; slot < buffer.getSlots(); slot++) {
            ItemStack existing = buffer.getStackInSlot(slot);
            if (existing.isEmpty()) {
                buffer.setStackInSlot(slot, stack.copy());
                return true;
            }
            if (ItemStack.isSameItemSameTags(existing, stack)
                    && existing.getCount() < buffer.getSlotLimit(slot)) {
                int room = buffer.getSlotLimit(slot) - existing.getCount();
                int moved = Math.min(room, stack.getCount());
                existing.grow(moved);
                buffer.setStackInSlot(slot, existing);
                return moved == stack.getCount();
            }
        }
        return false;
    }

    public boolean isStructureValid() {
        return structureValid;
    }

    public boolean isPowered() {
        return powered;
    }

    public ExtractorFuelTier getActiveFuelTier() {
        return activeFuelTier;
    }

    public Component statusLine() {
        if (!structureValid) {
            return Component.translatable("nodecore.extractor.status.invalid");
        }
        if (!powered) {
            return Component.translatable("nodecore.extractor.status.no_power");
        }
        if (isBufferFull()) {
            return Component.translatable("nodecore.extractor.status.backpressure");
        }
        return Component.translatable("nodecore.extractor.status.producing",
                linkedNodeType.getDisplayName(),
                linkedTier,
                activeFuelTier.name().toLowerCase());
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("LinkedNodeId")) {
            linkedNodeId = tag.getUUID("LinkedNodeId");
        } else {
            linkedNodeId = null;
        }
        linkedNodePos = BlockPos.of(tag.getLong("LinkedNodePos"));
        linkedNodeType = com.mrwizard94.nodecore.node.NodeType.byId(tag.getString("LinkedNodeType"));
        linkedTier = tag.getInt("LinkedTier");
        productionAccumulator = tag.getDouble("ProductionAccumulator");
        solidFuelBurnTicks = tag.getInt("SolidFuelBurnTicks");
        structureRecheckCooldown = tag.getInt("StructureRecheckCooldown");
        decayCooldown = tag.getInt("DecayCooldown");
        structureValid = tag.getBoolean("StructureValid");
        powered = tag.getBoolean("Powered");
        buffer.deserializeNBT(tag.getCompound("Buffer"));
        fuelSlot.deserializeNBT(tag.getCompound("Fuel"));
        lavaTank.readFromNBT(tag.getCompound("LavaTank"));
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        if (linkedNodeId != null) {
            tag.putUUID("LinkedNodeId", linkedNodeId);
        }
        tag.putLong("LinkedNodePos", linkedNodePos.asLong());
        tag.putString("LinkedNodeType", linkedNodeType.getId());
        tag.putInt("LinkedTier", linkedTier);
        tag.putDouble("ProductionAccumulator", productionAccumulator);
        tag.putInt("SolidFuelBurnTicks", solidFuelBurnTicks);
        tag.putInt("StructureRecheckCooldown", structureRecheckCooldown);
        tag.putInt("DecayCooldown", decayCooldown);
        tag.putBoolean("StructureValid", structureValid);
        tag.putBoolean("Powered", powered);
        tag.put("Buffer", buffer.serializeNBT());
        tag.put("Fuel", fuelSlot.serializeNBT());
        tag.put("LavaTank", lavaTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return externalItemCapability.cast();
        }
        if (capability == ForgeCapabilities.FLUID_HANDLER) {
            return fluidCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        externalItemCapability.invalidate();
        fluidCapability.invalidate();
        super.invalidateCaps();
    }
}