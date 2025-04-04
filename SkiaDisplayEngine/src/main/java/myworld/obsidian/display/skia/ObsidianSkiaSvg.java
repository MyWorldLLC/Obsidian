package myworld.obsidian.display.skia;

import io.github.humbleui.skija.svg.SVGDOM;
import myworld.obsidian.display.ObsidianSvg;

public class ObsidianSkiaSvg extends ObsidianSvg {

    protected final SVGDOM dom;

    public ObsidianSkiaSvg(SVGDOM dom){
        this.dom = dom;
    }

    protected SVGDOM getDom(){
        return dom;
    }

}
