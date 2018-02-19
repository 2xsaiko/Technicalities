package com.technicalitiesmc.base.init;

import com.technicalitiesmc.base.item.ItemBookManual;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictRegister {
    private OreDictRegister() {}

    public static void registerItems() {
        OreDictionary.registerOre("book", ItemBookManual.INSTANCE);
    }
}
