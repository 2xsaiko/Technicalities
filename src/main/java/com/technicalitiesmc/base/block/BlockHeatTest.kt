package com.technicalitiesmc.base.block

import com.technicalitiesmc.api.getHeatHandler
import com.technicalitiesmc.base.MODID
import net.minecraft.block.material.Material
import net.minecraft.item.ItemStack
import net.minecraft.util.ITickable
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.common.api.extensions.makeStack
import therealfarfetchd.quacklib.common.api.qblock.QBlock
import therealfarfetchd.quacklib.common.api.qblock.WrapperImplManager
import therealfarfetchd.quacklib.common.api.util.BlockDef

@BlockDef
class BlockHeatTest : QBlock(), ITickable {
    override fun update() {
        world.getHeatHandler().addEnergyToBlock(container, pos.up(), 500.0, 1880.6)
    }

    override val blockType: ResourceLocation = ResourceLocation(MODID, "heat_test")

    override val material: Material = Material.CAKE

    override fun getItem(): ItemStack = Item.makeStack()

    companion object {
        val Block by WrapperImplManager.container(BlockHeatTest::class)
        val Item by WrapperImplManager.item(BlockHeatTest::class)
    }
}