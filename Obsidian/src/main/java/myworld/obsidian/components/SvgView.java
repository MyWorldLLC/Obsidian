package myworld.obsidian.components;

import myworld.obsidian.display.ObsidianSvg;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.scene.Component;

public class SvgView extends Component {

    public static final String COMPONENT_STYLE_NAME = "SvgView";

    public static final String SVG_DATA_NAME = "svg";

    protected final ValueProperty<ObsidianSvg> svg;

    public SvgView(){
        styleName.set(COMPONENT_STYLE_NAME);

        svg = new ValueProperty<>();

        renderVars.put(SVG_DATA_NAME, svg);
    }

    public ValueProperty<ObsidianSvg> svg(){
        return svg;
    }

}
