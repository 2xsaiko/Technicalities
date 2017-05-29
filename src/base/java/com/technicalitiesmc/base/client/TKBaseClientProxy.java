package com.technicalitiesmc.base.client;

import com.technicalitiesmc.base.TKBaseCommonProxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class TKBaseClientProxy extends TKBaseCommonProxy {

    @Override
    public void registerItemModel(Item item, int meta, ModelResourceLocation location) {
        ModelLoader.setCustomModelResourceLocation(item, meta, location);
    }

}
