package com.technicalitiesmc.base.proxies

import com.technicalitiesmc.api.TechnicalitiesAPI
import com.technicalitiesmc.api.heat.IHeatConductor
import com.technicalitiesmc.api.heat.IWorldHeatHandler
import com.technicalitiesmc.base.MODID
import com.technicalitiesmc.base.Technicalities
import com.technicalitiesmc.base.event.OreEventHandler
import com.technicalitiesmc.base.init.TKHeatObjects
import com.technicalitiesmc.base.network.PacketGuiButton
import com.technicalitiesmc.base.network.TKGuiHandler
import com.technicalitiesmc.base.weather.WeatherHandler
import com.technicalitiesmc.energy.heat.HeatPropertyRegistry
import com.technicalitiesmc.energy.heat.WorldHeatHandler
import com.technicalitiesmc.energy.kinesis.KineticManager
import com.technicalitiesmc.energy.kinesis.KineticNode
import com.technicalitiesmc.energy.kinesis.PacketKineticUpdate
import com.technicalitiesmc.lib.simple.SimpleCapabilityManager
import com.technicalitiesmc.lib.simple.SimpleRegistryManager
import com.technicalitiesmc.lib.util.DefaultCapabilityProvider
import elec332.core.inventory.window.WindowManager
import elec332.core.main.ElecCoreRegistrar
import elec332.core.util.RegistryHelper
import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.relauncher.Side
import java.io.InputStream

open class TKCommonProxy {

    open fun preInit(e: FMLPreInitializationEvent) {
        ElecCoreRegistrar.GRIDHANDLERS.register(Technicalities.electricityGridHandler)
        WindowManager.INSTANCE.register(TKGuiHandler)

        // Init capabilities
        SimpleCapabilityManager.INSTANCE.init(Technicalities.asmTable)
        SimpleRegistryManager.INSTANCE.init(Technicalities.asmTable)

        MinecraftForge.EVENT_BUS.register(OreEventHandler())
        TechnicalitiesAPI.heatPropertyRegistry = HeatPropertyRegistry.INSTANCE
        TechnicalitiesAPI.kineticNodeProvider = ::KineticNode
        RegistryHelper.registerEmptyCapability(IHeatConductor::class.java)
        RegistryHelper.registerEmptyCapability(IWorldHeatHandler::class.java)
        DefaultCapabilityProvider.registerWorldCapabilityProvider(ResourceLocation(MODID, "heatapi"),
                TechnicalitiesAPI.worldHeatCap, { WorldHeatHandler() })
        WeatherHandler.preInit()
        TKHeatObjects.init()
        KineticManager.init()

        // Register packets
        Technicalities.networkHandler.registerPacket(PacketGuiButton::class.java, Side.SERVER)
        Technicalities.networkHandler.registerPacket(PacketKineticUpdate::class.java, Side.CLIENT)
    }

    open fun init(e: FMLInitializationEvent) {}

    open fun postInit(e: FMLPostInitializationEvent) {}

    open fun registerItemModel(item: Item, meta: Int, location: ModelResourceLocation) {}

    open fun registerItemModel(block: Block, meta: Int, location: ModelResourceLocation) {}

    open fun bindSpecialRenderers(dataTable: ASMDataTable) {}

    open fun getWorld(): World = error("not implemented")

    open fun isGamePaused(): Boolean = error("not implemented")

    open fun readResource(path: ResourceLocation): InputStream = error("not implemented")

    open fun schedule(side: Side, task: () -> Unit) {
        if (side == Side.SERVER) {
            FMLCommonHandler.instance().minecraftServerInstance.addScheduledTask(task)
        }
    }

}