package com.technicalitiesmc.base.network

import com.technicalitiesmc.base.manual.client.gui.GuiManual
import elec332.core.inventory.window.IWindowFactory
import elec332.core.inventory.window.IWindowHandler
import elec332.core.inventory.window.Window
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

object TKGuiHandler : IGuiHandler, IWindowHandler {
    enum class GuiId {
        BOOK_MANUAL;

        companion object {
            val VALUES = values().toList()
        }
    }

    override fun getServerGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? {
        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer?, world: World?, x: Int, y: Int, z: Int): Any? = when (GuiId.VALUES[ID]) {
        TKGuiHandler.GuiId.BOOK_MANUAL -> GuiManual()
        else -> null
    }

    override fun createWindow(ID: Byte, entityPlayer: EntityPlayer?, world: World, x: Int, y: Int, z: Int): Window? {
        val tile = world.getTileEntity(BlockPos(x, y, z))
        return when (tile) {
            is IWindowFactory -> (tile as IWindowFactory).createWindow()
            else -> null
        }
    }
}