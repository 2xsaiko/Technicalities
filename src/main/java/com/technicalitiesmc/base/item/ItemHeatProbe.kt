package com.technicalitiesmc.base.item

import com.technicalitiesmc.api.TechnicalitiesAPI.getHeatHandler
import com.technicalitiesmc.base.MODID
import com.technicalitiesmc.lib.item.ItemBase
import elec332.core.util.PlayerHelper
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumActionResult
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.api.extensions.isServer
import therealfarfetchd.quacklib.common.api.util.ItemDef

@ItemDef
object ItemHeatProbe : ItemBase() {
    init {
        registryName = ResourceLocation(MODID, "heat_probe")
    }

    override fun onItemUse(player: EntityPlayer, world: World, pos: BlockPos, hand: EnumHand?, facing: EnumFacing?, hitX: Float, hitY: Float, hitZ: Float): EnumActionResult {
        if (world.isServer) {
            val heatObject = world.getHeatHandler().getHeatObject(pos)
            PlayerHelper.sendMessageToPlayer(player, "Pos: " + pos)
            if (heatObject == null) {
                PlayerHelper.sendMessageToPlayer(player, "null")
                return EnumActionResult.SUCCESS
            }
            PlayerHelper.sendMessageToPlayer(player, "Temp: " + heatObject.temperature)
            PlayerHelper.sendMessageToPlayer(player, "Energy: " + heatObject.energy)
        }
        return EnumActionResult.SUCCESS
    }
}