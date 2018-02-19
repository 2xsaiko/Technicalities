package com.technicalitiesmc.api

import com.technicalitiesmc.api.electricity.IElectricityDevice
import com.technicalitiesmc.api.electricity.IEnergyObject
import com.technicalitiesmc.api.heat.IHeatConductor
import com.technicalitiesmc.api.heat.IHeatPropertyRegistry
import com.technicalitiesmc.api.heat.IWorldHeatHandler
import com.technicalitiesmc.api.mechanical.IKineticNode
import com.technicalitiesmc.api.weather.IWeatherSimulator
import net.minecraft.world.World
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.capabilities.CapabilityInject
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

object TechnicalitiesAPI {
    @JvmStatic
    var heatPropertyRegistry: IHeatPropertyRegistry by singleAssign()

    @JvmStatic
    var kineticNodeProvider: (IKineticNode.Host) -> IKineticNode by singleAssign()

    @JvmStatic
    var electricityCap: Capability<IElectricityDevice> by singleAssign()
        @JvmStatic @CapabilityInject(IEnergyObject::class) set

    @JvmStatic
    var heatConductorCap: Capability<IHeatConductor> by singleAssign()
        @JvmStatic @CapabilityInject(IHeatConductor::class) set

    @JvmStatic
    var worldHeatCap: Capability<IWorldHeatHandler> by singleAssign()
        @JvmStatic @CapabilityInject(IWorldHeatHandler::class) set

    @JvmStatic
    var worldWeatherCap: Capability<IWeatherSimulator> by singleAssign()
        @JvmStatic @CapabilityInject(IWeatherSimulator::class) set

    @JvmStatic
    @Deprecated("Compatibility only", ReplaceWith("getHeatHandler()", "com.technicalitiesmc.api.getHeatHandler"))
    fun World.getHeatHandler(): IWorldHeatHandler =
            getCapability(worldHeatCap, null)!!

    @JvmStatic
    @Deprecated("Compatibility only", ReplaceWith("getWeatherSimulator()", "com.technicalitiesmc.api.getWeatherSimulator"))
    fun World.getWeatherSimulator(): IWeatherSimulator =
            getCapability(worldWeatherCap, null)!!
}

fun World.getHeatHandler(): IWorldHeatHandler =
        getCapability(TechnicalitiesAPI.worldHeatCap, null)!!

fun World.getWeatherSimulator(): IWeatherSimulator =
        getCapability(TechnicalitiesAPI.worldWeatherCap, null)!!

fun <T : Any> singleAssign() = object : ReadWriteProperty<Any?, T> {
    lateinit var t: T

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        if (!::t.isInitialized) error("Value ${property.name} not initialized!")
        return t
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        if (::t.isInitialized) error("Value ${property.name} already set!")
        t = value
    }
}