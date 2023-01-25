package myworld.obsidian.display;

import io.github.humbleui.skija.svg.SVGDOM;

public class ObsidianSvg {

    protected final SVGDOM dom;

    public ObsidianSvg(SVGDOM dom){
        this.dom = dom;
    }

    protected SVGDOM getDom(){
        return dom;
    }

}
