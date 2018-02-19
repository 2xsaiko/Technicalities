package com.technicalitiesmc.base.item

import com.technicalitiesmc.base.MODID
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import therealfarfetchd.quacklib.common.api.util.ItemDef

@ItemDef
object ItemReedStick : Item() {
    init {
        registryName = ResourceLocation(MODID, "reed_stick")
    }
}