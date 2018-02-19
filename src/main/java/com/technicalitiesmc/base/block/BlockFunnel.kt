package com.technicalitiesmc.base.block

import com.technicalitiesmc.base.MODID
import com.technicalitiesmc.lib.block.BlockBase
import com.technicalitiesmc.lib.item.ItemBlockBase
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.util.ResourceLocation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import therealfarfetchd.quacklib.common.api.util.BlockClassLayout
import therealfarfetchd.quacklib.common.api.util.BlockDef
import therealfarfetchd.quacklib.common.api.util.math.Vec3
import therealfarfetchd.quacklib.common.api.util.math.toVec3

@BlockDef(layout = BlockClassLayout.StaticBlock)
object BlockFunnel : BlockBase(Material.GLASS) {
    init {
        registryName = ResourceLocation(MODID, "funnel")
        setHardness(1.0f)
    }

    override fun onEntityCollidedWithBlock(worldIn: World?, pos: BlockPos, state: IBlockState?, entityIn: Entity) {
        super.onEntityCollidedWithBlock(worldIn, pos, state, entityIn)
        val toCenter = Vec3(pos.x, pos.y, pos.z) + Vec3(0.5, 0.5, 0.5) - entityIn.positionVector.toVec3()
        entityIn.motionX = entityIn.motionX * 0.5 + toCenter.x
        entityIn.motionY = maxOf(-0.2, entityIn.motionY)
        entityIn.motionZ = entityIn.motionZ * 0.5 + toCenter.z
    }

    override fun addCollisionBoxes(state: IBlockState?, world: World?, pos: BlockPos?, boxes: MutableList<AxisAlignedBB>) {
        boxes.addAll(collision)
    }

    override fun isFull(state: IBlockState?): Boolean = false

    val Item = ItemBlockBase(this)

    val collision = listOf(
            AxisAlignedBB(0.0, 0.4375, 0.0, 0.0625, 1.0, 1.0),
            AxisAlignedBB(0.0, 0.4375, 0.0, 1.0, 1.0, 0.0625),
            AxisAlignedBB(0.9375, 0.4375, 0.0, 1.0, 1.0, 1.0),
            AxisAlignedBB(0.0, 0.4375, 0.9375, 1.0, 1.0, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 0.3125, 0.4375, 1.0),
            AxisAlignedBB(0.6875, 0.0, 0.0, 1.0, 0.4375, 1.0),
            AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.4375, 0.3125),
            AxisAlignedBB(0.0, 0.0, 0.6875, 1.0, 0.4375, 1.0)
    )
}