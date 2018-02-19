package com.technicalitiesmc.base.item

import com.technicalitiesmc.base.MODID
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.CraftingManager
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.util.Constants.NBT.TAG_COMPOUND
import therealfarfetchd.quacklib.common.api.util.ItemDef
import therealfarfetchd.quacklib.common.api.util.QNBTCompound

@ItemDef
object ItemRecipeBook : Item() {
    init {
        registryName = ResourceLocation(MODID, "recipe_book")
        maxStackSize = 1
    }

    fun getRecipes(book: ItemStack): List<Recipe> {
        if (book.isEmpty) return emptyList()
        val tagCompound = book.tagCompound ?: return emptyList()
        return tagCompound.getTagList("recipes", TAG_COMPOUND).let {
            (0 until it.tagCount())
                    .map(it::getCompoundTagAt)
                    .map(::QNBTCompound)
                    .map { tag -> Recipe().also { it.loadData(tag) } }
        }
    }

    fun addRecipe(book: ItemStack, recipe: Recipe): Boolean {
        val tag = book.tagCompound?.let(::QNBTCompound)
                ?: QNBTCompound().also { book.tagCompound = it.self }
        val recipes = tag.self.getTagList("recipes", TAG_COMPOUND)
        if (recipes.tagCount() >= 32) return false
        val data = QNBTCompound().also(recipe::saveData)
        if (recipes.any { it == data.self }) return false
        recipes.appendTag(data.self)
        tag.self.setTag("recipes", recipes)
        return true
    }

    fun removeRecipe(book: ItemStack, recipe: Int): Boolean {
        val tag = book.tagCompound?.let(::QNBTCompound)
                ?: QNBTCompound().also { book.tagCompound = it.self }
        val recipes = tag.self.getTagList("recipes", TAG_COMPOUND)
        if (recipe !in 0 until recipes.tagCount()) return false
        recipes.removeTag(recipe)
        tag.self.setTag("recipes", recipes)
        return true
    }

    class Recipe {
        private val _grid: Array<ItemStack> = Array(9) { ItemStack.EMPTY }
        val grid
            get() = _grid.toList()

        var result: ItemStack = ItemStack.EMPTY
            private set

        var remainder: List<ItemStack> = emptyList()
            private set

        fun saveData(nbt: QNBTCompound) {
            val grid = NBTTagList()
            this.grid.forEach { grid.appendTag(it.serializeNBT()) }
            nbt.self.setTag("grid", grid)

            nbt.nbt["result"] = QNBTCompound(result.serializeNBT())

            val remainder = NBTTagList()
            this.remainder.forEach { remainder.appendTag(it.serializeNBT()) }
            nbt.self.setTag("remainder", remainder)
        }

        fun loadData(nbt: QNBTCompound) {
            val grid = nbt.self.getTagList("grid", TAG_COMPOUND)
            _grid.indices.forEach { _grid[it] = ItemStack(grid.getCompoundTagAt(it)) }
            result = ItemStack(nbt.nbt["result"].self)
            remainder = nbt.self.getTagList("remainder", TAG_COMPOUND)
                    .let { (0 until it.tagCount()).map(it::getCompoundTagAt).map(::ItemStack) }
        }

        companion object {
            fun fromGrid(craftingGrid: InventoryCrafting, world: World): Recipe? {
                val mcRecipe = CraftingManager.findMatchingRecipe(craftingGrid, world) ?: return null
                val recipe = Recipe()
                for (i in 0 until 9) recipe._grid[i] = craftingGrid.getStackInSlot(i).copy().splitStack(1)
                recipe.result = mcRecipe.getCraftingResult(craftingGrid)
                mcRecipe.getRemainingItems(craftingGrid)
                        .filterNot { it.isEmpty() }
                        .forEach { recipe.remainder += it }
                return recipe
            }
        }
    }
}