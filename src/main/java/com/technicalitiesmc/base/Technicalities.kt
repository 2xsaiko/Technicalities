package com.technicalitiesmc.base

import com.technicalitiesmc.base.proxies.TKCommonProxy
import com.technicalitiesmc.energy.electricity.grid.ElectricityGridHandler
import elec332.core.api.network.INetworkHandler
import elec332.core.api.network.ModNetworkHandler
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import java.io.File

const val MODID = "technicalities"

@Mod(modid = MODID, modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object Technicalities {
    @JvmStatic
    val log: Logger = LogManager.getLogger(MODID)

    @SidedProxy(serverSide = "com.technicalitiesmc.base.proxies.TKCommonProxy", clientSide = "com.technicalitiesmc.base.proxies.TKClientProxy")
    @JvmStatic
    lateinit var proxy: TKCommonProxy

    @JvmStatic
    lateinit var baseFolder: File // All config files of submods can go in here
        private set

    @ModNetworkHandler
    @JvmStatic
    lateinit var networkHandler: INetworkHandler
        private set

    @JvmStatic
    val electricityGridHandler = ElectricityGridHandler()

    @JvmSynthetic
    internal lateinit var asmTable: ASMDataTable

    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        Technicalities.asmTable = e.asmData
        Technicalities.baseFolder = File(e.modConfigurationDirectory, "MODID")
        proxy.preInit(e)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        proxy.init(e)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        proxy.postInit(e)
    }
}