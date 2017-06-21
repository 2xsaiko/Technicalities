package com.technicalitiesmc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.technicalitiesmc.lib.TKLib;
import com.technicalitiesmc.lib.funcint.LambdaUtils;
import com.technicalitiesmc.lib.module.ModuleManager;
import com.technicalitiesmc.lib.util.JSONUtils;

import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.discovery.ASMDataTable.ASMData;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Technicalities' module manager.<br/>
 * Finds and loads all enabled submodules.
 */
public class TKModuleManager extends ModuleManager<ITKModule> {

    private static final File configFile = new File("./config/" + Technicalities.MODID + "/modules.json");
    // Lazy-loaded config file
    private static final Supplier<JsonObject> config = Suppliers.memoize(() -> {
        if (configFile.exists()) {
            try {
                return JSONUtils.read(new FileInputStream(configFile), JsonObject.class);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return new JsonObject();
    });

    TKModuleManager() {
        super(ITKModule.class, TKModule.class, TKModuleManager::test, TKModuleManager::compare, TKModule::value, TKModule::dependencies);
    }

    /**
     * Saves the modules config file to disk.
     */
    public void save() {
        try {
            JsonObject cfg = config.get();
            if (configFile.exists()) {
                configFile.delete();
            }
            configFile.getParentFile().mkdirs();
            configFile.createNewFile();
            JSONUtils.write(new FileOutputStream(configFile), cfg);
        } catch (Exception ex) {
            throw Throwables.propagate(ex);
        }
    }

    /**
     * Checks the config file to see if a module should be loaded or not.
     */
    private static boolean test(Class<? extends ITKModule> type, TKModule annotation) {
        if (!annotation.canBeDisabled()) {
            return true;
        }
        JsonObject cfg = config.get();
        if (cfg.has(annotation.value())) {
            return cfg.get(annotation.value()).getAsBoolean();
        }
        boolean enabled = annotation.enabledByDefault();
        cfg.add(annotation.value(), new JsonPrimitive(enabled));
        return enabled;
    }

    /**
     * Compares two modules. Used for dependency sorting. Crashes if a circular dependency is found.
     */
    private static int compare(Pair<Class<? extends ITKModule>, TKModule> modA, Pair<Class<? extends ITKModule>, TKModule> modB) {
        TKModule a = modA.getValue(), b = modB.getValue();
        if (ArrayUtils.contains(a.dependencies(), a.value()) || ArrayUtils.contains(a.after(), a.value())) {
            throw new IllegalStateException("Module " + a.value() + " cannot depend on itself.");
        }
        if (ArrayUtils.contains(b.dependencies(), b.value()) || ArrayUtils.contains(b.after(), b.value())) {
            throw new IllegalStateException("Module " + b.value() + " cannot depend on itself.");
        }
        boolean aBefore = ArrayUtils.contains(b.dependencies(), a.value()) || ArrayUtils.contains(b.after(), a.value());
        boolean bBefore = ArrayUtils.contains(a.dependencies(), b.value()) || ArrayUtils.contains(a.after(), b.value());
        if (aBefore && bBefore) {
            throw new IllegalStateException("Circular dependency found between modules " + a.value() + " and " + b.value() + ".");
        }
        return aBefore ? -1 : bBefore ? 1 : 0;
    }

    /**
     * Gets the internal name of a module.
     */
    static String getName(ITKModule module) {
        return module.getClass().getAnnotation(TKModule.class).value();
    }

    /**
     * Initializes the proxies in all the loaded modules.
     */
    void initProxies() {
        List<String> loadedModules = getModules().stream().map(TKModuleManager::getName).collect(Collectors.toList());
        TKLib.asmTable.getAll(ModuleProxy.class.getName())//
                .stream()//
                .filter(d -> loadedModules.contains(d.getAnnotationInfo().get("module")))//
                .forEach(LambdaUtils.safeConsumer(this::initProxy));
    }

    /**
     * Gets the proxy class for the current {@link Side} and instantiates it, then sets the annotated field to it.
     */
    private void initProxy(ASMData data) throws Exception {
        String loadedSide = FMLCommonHandler.instance().getSide() == Side.CLIENT ? "clientSide" : "serverSide";
        Object proxy = Class.forName((String) data.getAnnotationInfo().get(loadedSide)).newInstance();
        Class.forName(data.getClassName()).getDeclaredField(data.getObjectName()).set(null, proxy);
    }

}