package myworld.obsidian.display.skin.chipmunk;

import chipmunk.vm.ChipmunkVM;
import chipmunk.vm.ModuleLoader;
import myworld.obsidian.display.skin.UISkin;

import java.util.HashMap;
import java.util.Map;

public class ChipmunkSkinLoader {

    protected class Result {
        final Map<String, Object> vars = new HashMap<>();
        final Map<String, Object> styles = new HashMap<>();
        final Map<String, Map<String, String>> components = new HashMap<>();
    }

    public static UISkin load(String path, PathResolver resolver) throws Exception {

        var vm = new ChipmunkVM();
        var loader = new ModuleLoader();
        loader.registerNativeFactory(SkinModule.MODULE_NAME, SkinModule::new);

        return null;
    }

}
