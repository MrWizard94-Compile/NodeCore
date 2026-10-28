package com.mrwizard94.nodecore.blockentity;

import com.mrwizard94.nodecore.block.extractor.ExtractorFuelTier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
public final class ExtractorMultiblockItemHandler implements IItemHandler {
    private final IItemHandler fuel;
    private final IItemHandler buffer;

    public ExtractorMultiblockItemHandler(IItemHandler fuel, IItemHandler buffer) {
        this.fuel = fuel;
        this.buffer = buffer;
    }

    @Override
    public int getSlots() {
        return buffer.getSlots();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return buffer.getStackInSlot(slot);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty() || ExtractorFuelTier.fromSolidItem(stack) == ExtractorFuelTier.NONE) {
            return stack;
        }
        return fuel.insertItem(0, stack, simulate);
    }

    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return buffer.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return buffer.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        return buffer.isItemValid(slot, stack);
    }
}