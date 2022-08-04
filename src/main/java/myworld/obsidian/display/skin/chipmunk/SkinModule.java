package myworld.obsidian.display.skin.chipmunk;

import chipmunk.runtime.ChipmunkModule;
import chipmunk.vm.invoke.security.AllowChipmunkLinkage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class SkinModule implements ChipmunkModule {

    public static final String MODULE_NAME = "obsidian.skin";

    protected Consumer<List<String>> helperLoader;
    protected Consumer<List<String>> componentLoader;

    protected String name;
    protected String description;

    @AllowChipmunkLinkage
    public void skin(String name, String description){
        this.name = name;
        this.description = description;
    }

    @AllowChipmunkLinkage
    public void helpers(List<String> scriptPaths){
        helperLoader.accept(scriptPaths);
    }

    @AllowChipmunkLinkage
    public void components(List<String> scriptPaths){
        componentLoader.accept(scriptPaths);
    }

    public String getSkinName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setHelperLoader(Consumer<List<String>> helperLoader){
        this.helperLoader = helperLoader;
    }

    public void setComponentLoader(Consumer<List<String>> componentLoader){
        this.componentLoader = componentLoader;
    }

}
