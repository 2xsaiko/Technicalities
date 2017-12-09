package com.technicalitiesmc.base.proxies;

import com.google.common.base.Throwables;
import com.technicalitiesmc.base.client.EmptyModelLoader;
import com.technicalitiesmc.lib.client.SpecialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import org.objectweb.asm.Type;

import java.io.IOException;
import java.io.InputStream;

public class TKClientProxy extends TKCommonProxy {

    @Override
    public void preInit() {
        super.preInit();
        ModelLoaderRegistry.registerLoader(EmptyModelLoader.INSTANCE);
    }

    @Override
    public void registerItemModel(Item item, int meta, ModelResourceLocation location) {
        ModelLoader.setCustomModelResourceLocation(item, meta, location);
    }

    @Override
    public void bindSpecialRenderers(ASMDataTable asmDataTable) {
        for (ASMData data : asmDataTable.getAll(SpecialRenderer.class.getName())) {
            try {
                bindTileEntitySpecialRenderer(Class.forName(data.getClassName()), (TileEntitySpecialRenderer<?>) Class
                        .forName(((Type) data.getAnnotationInfo().get("value")).getClassName()).newInstance());
            } catch (Exception ex) {
                throw Throwables.propagate(ex);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends TileEntity> void bindTileEntitySpecialRenderer(Class<?> tileType, TileEntitySpecialRenderer<?> tesr) {
        ClientRegistry.bindTileEntitySpecialRenderer((Class<T>) tileType, (TileEntitySpecialRenderer<T>) tesr);
    }

    @Override
    public World getWorld() {
        return Minecraft.getMinecraft().world;
    }

    @Override
    public boolean isGamePaused() {
        return Minecraft.getMinecraft().isGamePaused();
    }

    @Override
    public InputStream readResource(ResourceLocation path) throws IOException {
        return Minecraft.getMinecraft().getResourceManager().getResource(path).getInputStream();
    }

}