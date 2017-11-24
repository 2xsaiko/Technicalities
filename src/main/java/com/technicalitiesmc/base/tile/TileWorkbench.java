package com.technicalitiesmc.base.tile;

import com.technicalitiesmc.base.init.TKBaseItems;
import com.technicalitiesmc.base.item.ItemRecipeBook;
import com.technicalitiesmc.base.item.ItemRecipeBook.Recipe;
import com.technicalitiesmc.util.block.TileBase;
import com.technicalitiesmc.util.inventory.SimpleItemHandler;
import com.technicalitiesmc.util.stack.StackList;
import elec332.core.inventory.ContainerNull;
import elec332.core.util.NBTHelper;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemHandlerHelper;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class TileWorkbench extends TileBase {

    public static final int INV_START = 0, INV_SIZE = 2 * 9;
    public static final int GRID_START = INV_START + INV_SIZE, GRID_SIZE = 3 * 3 + 1;
    public static final int BOOK_START = GRID_START + GRID_SIZE, BOOK_SIZE = 1 + 1;
    public static final int INVENTORY_SIZE = BOOK_START + BOOK_SIZE;

    private final SimpleItemHandler inventory = new SimpleItemHandler(INVENTORY_SIZE, this::onInventoryUpdate);
    private final ExposedInventory exposedInventory = new ExposedInventory();

    private final GridInventory craftingGrid = new GridInventory();
    private final InventoryCrafting inventoryCrafting = new InventoryCrafting(new ContainerNull(), 3, 3);

    private final List<Pair<Recipe, StackList>> recipes = new LinkedList<>();

    private int ink = 0;

    public TileWorkbench() {
        inventory.withFilter(TKBaseItems.recipe_book, BOOK_START);
        inventory.withFilter(new ItemStack(Items.DYE, 1, 0), BOOK_START + 1);
    }

    public SimpleItemHandler getInventory() {
        return inventory;
    }

    public ExposedInventory getExposedInventory() {
        return exposedInventory;
    }

    public GridInventory getCraftingGrid() {
        return craftingGrid;
    }

    public InventoryCrafting getGrid() {
        return inventoryCrafting;
    }

    public List<Pair<Recipe, StackList>> getRecipes() {
        return recipes;
    }

    public int getInk() {
        return ink;
    }

    public boolean consumeInk(boolean simulated) {
        if (ink == 0) {
            ItemStack stack = inventory.getStackInSlot(BOOK_START + 1);
            if (!stack.isEmpty()) {
                if (simulated) {
                    return true;
                } else {
                    stack.shrink(1);
                    ink = 12;
                }
            }
        }
        if (ink == 0) {
            return false;
        }
        if (!simulated) {
            ink--;
            sendPacket(4, new NBTHelper().addToTag(ink, "ink").serializeNBT());
        }
        return true;
    }

    private void onInventoryUpdate(int slot) {
        if (slot >= INV_START && slot < INV_START + INV_SIZE) {
            updateAvailableRecipes();
        } else if (slot == BOOK_START) {
            updateRecipeBook();
        } else if (slot >= GRID_START && slot < GRID_START + GRID_SIZE) {
            refreshRecipe(slot);
            updateAvailableRecipes();
        }

        markDirty();
    }

    private void updateAvailableRecipes() {
        recipes.replaceAll(pair -> Pair.of(pair.getKey(), computeRequirements(pair.getKey())));
    }

    private void updateRecipeBook() {
        recipes.clear();
        for (Recipe recipe : ItemRecipeBook.getRecipes(inventory.getStackInSlot(BOOK_START))) {
            recipes.add(Pair.of(recipe, computeRequirements(recipe)));
        }
    }

    private StackList computeRequirements(Recipe recipe) {
        StackList required = new StackList();
        for (ItemStack stack : recipe.getGrid()) {
            if (!stack.isEmpty()) {
                stack = stack.copy();
                stack.setCount(1);
                required.add(stack);
            }
        }

        for (ItemStack stack : exposedInventory) {
            required.remove(stack, stack.getCount());
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = craftingGrid.getStackInSlot(i);
            required.remove(stack, stack.getCount());
        }
        return required;
    }

    private void refreshRecipe(int slot) {
        if (slot < GRID_START + GRID_SIZE - 1) {
            inventoryCrafting.stackList.set(slot - GRID_START, inventory.getStackInSlot(slot));
        } else if (!craftingGrid.getStackInSlot(9).isEmpty()) {
            markDirty();
            return;
        }
        refreshRecipe();
    }

    private void refreshRecipe() {
        IRecipe recipe = CraftingManager.findMatchingRecipe(inventoryCrafting, getWorld());
        if (recipe != null) {
            inventory.setStackInSlot(GRID_START + 9, recipe.getCraftingResult(inventoryCrafting));
        } else {
            inventory.getContents().set(GRID_START + 9, ItemStack.EMPTY);
        }
    }

    public void pullStack(ItemStack stack) {
        for (ItemStack s : exposedInventory) {
            if (ItemHandlerHelper.canItemStacksStack(s, stack)) {
                s.shrink(1);
                return;
            }
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return (T) exposedInventory;
        }
        return super.getCapability(capability, facing);
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        tag.setTag("inventory", inventory.serializeNBT());
        tag.setInteger("ink", ink);
        return tag;
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        inventory.deserializeNBT(tag.getCompoundTag("inventory"));
        ink = tag.getInteger("ink");

        updateRecipeBook();

        for (int i = 0; i < 9; i++) {
            inventoryCrafting.stackList.set(i, inventory.getStackInSlot(i));
        }
        refreshRecipe();
    }

    @Override
    public void onDataPacket(int id, NBTTagCompound tag) {
        if (id == 4){
            ink = tag.getInteger("ink");
        } else {
            super.onDataPacket(id, tag);
        }
    }

    public class ExposedInventory implements IItemHandler, Iterable<ItemStack> {

        private ExposedInventory() {
        }

        @Override
        public int getSlots() {
            return INV_SIZE;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return slot < INV_SIZE ? inventory.getStackInSlot(slot) : ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (slot < INV_SIZE) {
                return inventory.insertItem(slot, stack, simulate);
            }
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot < INV_SIZE) {
                return inventory.extractItem(slot, amount, simulate);
            }
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot < INV_SIZE) {
                return inventory.getSlotLimit(slot);
            }
            return 0;
        }

        @Nonnull
        @Override
        public Iterator<ItemStack> iterator() {
            return new Iterator<ItemStack>() {

                private int i = 0;

                @Override
                public boolean hasNext() {
                    return i < getSlots();
                }

                @Override
                public ItemStack next() {
                    return getStackInSlot(i++);
                }

            };
        }

    }

    public class GridInventory implements IItemHandlerModifiable {

        @Override
        public int getSlots() {
            return GRID_SIZE;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return inventory.getStackInSlot(GRID_START + slot);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return inventory.insertItem(GRID_START + slot, stack, simulate);
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot == 9 && !simulate) {
                IRecipe recipe = CraftingManager.findMatchingRecipe(inventoryCrafting, getWorld());
                if (recipe != null) {
                    inventory.setStackInSlot(GRID_START + 9, recipe.getCraftingResult(inventoryCrafting));
                    // TODO: Add leftovers
                    for (int i = 0; i < 9; i++) {
                        extractCraftingComponent(i);
                    }
                } else {
                    return ItemStack.EMPTY;
                }
            }
            return inventory.extractItem(GRID_START + slot, amount, simulate);
        }

        private void extractCraftingComponent(int slot) {
            ItemStack stack = getStackInSlot(slot);
            if (stack.isEmpty()) {
                return;
            }

            if (stack.getCount() > 1) {
                stack.shrink(1);
                return;
            }

            for (ItemStack s : exposedInventory) {
                if (ItemHandlerHelper.canItemStacksStack(s, stack)) {
                    s.shrink(1);
                    return;
                }
            }

            stack.shrink(1);
        }

        @Override
        public int getSlotLimit(int slot) {
            return inventory.getSlotLimit(GRID_START + slot);
        }

        @Override
        public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
            inventory.setStackInSlot(GRID_START + slot, stack);
        }

    }

}