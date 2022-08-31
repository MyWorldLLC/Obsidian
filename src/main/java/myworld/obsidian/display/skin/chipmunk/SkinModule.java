package myworld.obsidian.display.skin.chipmunk;

import chipmunk.runtime.ChipmunkModule;
import chipmunk.vm.invoke.security.AllowChipmunkLinkage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class SkinModule implements ChipmunkModule {

    public static final String MODULE_NAME = "obsidian.skin";

    protected final List<String> helpers = new ArrayList<>();
    protected final List<String> components = new ArrayList<>();

    protected String name;
    protected String description;

    @AllowChipmunkLinkage
    public void name(String name){
        this.name = name;
    }

    @AllowChipmunkLinkage
    public void description(String description){
        this.description = description;
    }

    @AllowChipmunkLinkage
    public void helpers(List<String> scriptPaths){
        helpers.addAll(scriptPaths);
    }

    @AllowChipmunkLinkage
    public void components(List<String> scriptPaths){
        components.addAll(scriptPaths);
    }

    public String getSkinName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getHelpers(){
        return helpers;
    }

    public List<String> getComponents(){
        return components;
    }

}
