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

    protected final Map<String, Map<String, Object>> styles = new HashMap<>();
    protected final Map<String, Map<String, String>> components = new HashMap<>();

    @AllowChipmunkLinkage
    protected final Map<String, Object> vars = new HashMap<>();

    @AllowChipmunkLinkage
    public void style(String name, Map<String, Object> style){
        styles.put(name, style);
    }

    @AllowChipmunkLinkage
    public void component(String name, Map<String, String> component){
        components.put(name, component);
    }

    @AllowChipmunkLinkage
    public void component(String name, Map<String, Object> component, Map<String, Map<String, Object>> stateStyles){

    }

    public Map<String, Map<String, Object>> getStyles(){
        return styles;
    }

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

    public Map<String, Object> getVars(){
        return vars;
    }

}
