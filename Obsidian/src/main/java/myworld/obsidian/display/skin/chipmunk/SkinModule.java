package myworld.obsidian.display.skin.chipmunk;

import chipmunk.runtime.ChipmunkModule;
import chipmunk.vm.invoke.security.AllowChipmunkLinkage;

import java.util.ArrayList;
import java.util.List;

public class SkinModule implements ChipmunkModule {

    public static final String MODULE_NAME = "obsidian.skin";

    protected final List<String> helpers = new ArrayList<>();
    protected final List<String> components = new ArrayList<>();
    protected final List<String> fonts = new ArrayList<>();
    protected final List<String> images = new ArrayList<>();
    protected final List<String> svgs = new ArrayList<>();

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

    @AllowChipmunkLinkage
    public void fonts(List<String> fontPaths){
        fonts.addAll(fontPaths);
    }

    @AllowChipmunkLinkage
    public void images(List<String> imagePaths){
        imagePaths.forEach(p -> {
            if(p.endsWith(".svg")){
                svgs.add(p);
            }else{
                images.add(p);
            }
        });
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

    public List<String> getFonts(){
        return fonts;
    }

    public List<String> getImages(){
        return images;
    }

    public List<String> getSvgs(){
        return svgs;
    }

}
