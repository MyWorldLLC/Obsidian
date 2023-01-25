package myworld.obsidian.display;

import io.github.humbleui.skija.svg.SVGDOM;

public class Svg {

    protected final SVGDOM dom;

    public Svg(SVGDOM dom){
        this.dom = dom;
    }

    protected SVGDOM getDom(){
        return dom;
    }

}
