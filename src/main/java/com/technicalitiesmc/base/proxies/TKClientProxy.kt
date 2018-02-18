package com.technicalitiesmc.base.proxies

import com.technicalitiesmc.base.MODID
import com.technicalitiesmc.base.Technicalities
import com.technicalitiesmc.base.client.EmptyModelLoader
import com.technicalitiesmc.base.manual.api.ManualAPI
import com.technicalitiesmc.base.manual.api.prefab.manual.ResourceContentProvider
import com.technicalitiesmc.base.manual.api.prefab.manual.TextureTabIconRenderer
import com.technicalitiesmc.base.manual.client.manual.provider.*
import com.technicalitiesmc.base.manual.common.api.ManualAPIImpl
import com.technicalitiesmc.base.network.TKGuiHandler
import com.technicalitiesmc.lib.client.SpecialRenderer
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ResourceLocation
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.client.model.ModelLoaderRegistry
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.relauncher.Side
import org.objectweb.asm.Type
import java.io.InputStream

class TKClientProxy : TKCommonProxy() {

    override fun preInit(e: FMLPreInitializationEvent) {
        super.preInit(e)
        ModelLoaderRegistry.registerLoader(EmptyModelLoader.INSTANCE)
    }

    override fun init(e: FMLInitializationEvent) {
        super.init(e)
        bindSpecialRenderers(Technicalities.asmTable)
        NetworkRegistry.INSTANCE.registerGuiHandler(Technicalities, TKGuiHandler)
        initManual()
    }

    private fun initManual() {
        ManualAPI.addProvider(DefinitionPathProvider())
        ManualAPI.addProvider(ResourceContentProvider(MODID, "docs/", false))
        ManualAPI.addProvider(ResourceContentProvider(MODID, "docs_tldr/", true))
        ManualAPI.addProvider("", TextureImageProvider())
        ManualAPI.addProvider("item", ItemImageProvider())
        ManualAPI.addProvider("block", BlockImageProvider())
        ManualAPI.addProvider("oredict", OreDictImageProvider())
        ManualAPI.addTab(TextureTabIconRenderer(ResourceLocation(MODID, "textures/gui/manual_home.png")), "technicalities.manual.home", "%LANGUAGE%/index.md")

        val enterTLDRMode = ResourceLocation(MODID, "textures/gui/tldr.png")
        val exitTLDRMode = ResourceLocation(MODID, "textures/gui/tldr_exit.png")
        ManualAPI.addTab(PageDependentTabProvider(
                { if (ManualAPIImpl.isTLDRMode()) exitTLDRMode else enterTLDRMode },
                { if (ManualAPIImpl.isTLDRMode()) "technicalities.manual.tldr_exit" else "technicalities.manual.tldr" },
                { ManualAPIImpl.toggleTLDRMode(); it })
        )
    }

    override fun registerItemModel(item: Item, meta: Int, location: ModelResourceLocation) {
        ModelLoader.setCustomModelResourceLocation(item, meta, location)
    }

    @Suppress("UNCHECKED_CAST")
    override fun bindSpecialRenderers(dataTable: ASMDataTable) {
        for (data in dataTable.getAll(SpecialRenderer::class.java.name)) {
            bindTileEntitySpecialRenderer<TileEntity>(
                    Class.forName(data.className) as Class<TileEntity>,
                    Class.forName((data.annotationInfo["value"] as Type).className).newInstance() as TileEntitySpecialRenderer<TileEntity>
            )
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T : TileEntity> bindTileEntitySpecialRenderer(tileType: Class<*>, tesr: TileEntitySpecialRenderer<*>) {
        ClientRegistry.bindTileEntitySpecialRenderer(tileType as Class<T>, tesr as TileEntitySpecialRenderer<T>)
    }

    override fun getWorld(): World = Minecraft.getMinecraft().world

    override fun isGamePaused(): Boolean = Minecraft.getMinecraft().isGamePaused()

    override fun readResource(path: ResourceLocation): InputStream =
            Minecraft.getMinecraft().resourceManager.getResource(path).inputStream

    override fun schedule(side: Side, task: () -> Unit) {
        super.schedule(side, task)
        if (side == Side.CLIENT) {
            Minecraft.getMinecraft().addScheduledTask(task)
        }
    }

}