package myworld.obsidian.display.skin.chipmunk;

import chipmunk.compiler.Compilation;
import chipmunk.vm.ChipmunkVM;
import chipmunk.vm.ModuleLoader;
import chipmunk.vm.jvm.JvmCompilerConfig;
import myworld.obsidian.display.skin.UISkin;

import java.util.HashMap;
import java.util.Map;

public class ChipmunkSkinLoader {

    public static final String DEFAULT_SKIN = "/myworld/obsidian/skin/default/Skin.chp";

    protected class Result {
        final Map<String, Object> vars = new HashMap<>();
        final Map<String, Object> styles = new HashMap<>();
        final Map<String, Map<String, String>> components = new HashMap<>();
    }

    public static UISkin load(String path, PathResolver resolver) throws Exception {
        var vm = new ChipmunkVM();
        try{
            vm.start();

            var loader = new ModuleLoader();
            loader.registerNativeFactory(SkinModule.MODULE_NAME, SkinModule::new);
            loader.registerNativeFactory(VarModule.MODULE_NAME, VarModule::new);

            var skinModule = (SkinModule) loader.loadNative(SkinModule.MODULE_NAME);

            // TODO - this won't work for a custom entrypoint
            var script = vm.compileScript(resolver.resolve(path), path);
            script.getModuleLoader().setDelegate(loader);
            script.run();

            var skin = new UISkin(skinModule.getSkinName());


            vm.stop();

            // TODO - populate skin data

            return skin;
        }finally{
            vm.stop();
        }

    }

    public static UISkin loadFromClasspath(String path) throws Exception {
        return load(path, ChipmunkSkinLoader.class::getResourceAsStream);
    }

}
