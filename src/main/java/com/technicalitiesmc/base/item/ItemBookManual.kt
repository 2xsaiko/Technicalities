package com.technicalitiesmc.base.item

import com.technicalitiesmc.base.MODID
import com.technicalitiesmc.base.manual.api.ManualAPI
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.*
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.api.extensions.isClient
import therealfarfetchd.quacklib.common.api.extensions.select
import therealfarfetchd.quacklib.common.api.util.ItemDef

/**
 * The manual!
 */
@ItemDef
object ItemBookManual : Item() {
    init {
        registryName = ResourceLocation(MODID, "book_manual")
        maxStackSize = 1
    }

    fun tryOpenManual(world: World, player: EntityPlayer, path: String): Boolean {
        if (world.isClient) {
            ManualAPI.openFor(player)
            ManualAPI.reset()
            ManualAPI.navigate(path)
        }
        return true
    }

    override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        val path = ManualAPI.pathFor(world, pos) ?: return EnumActionResult.PASS
        return tryOpenManual(world, player, path).select(EnumActionResult.SUCCESS, EnumActionResult.PASS)
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand?): ActionResult<ItemStack> {
        if (world.isClient) {
            if (player.isSneaking) ManualAPI.reset()
            ManualAPI.openFor(player)
        }
        return super.onItemRightClick(world, player, hand)
    }
}