package myworld.obsidian.display.skin.builder;

import myworld.obsidian.display.skin.ComponentInterface;
import myworld.obsidian.display.skin.ComponentSkin;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.VarType;

import java.util.ArrayList;
import java.util.List;

public class ComponentSkinBuilder {

    protected final String name;
    protected final ComponentInterface parameterInterface;
    protected final List<StyleClass> styles;

    protected ComponentSkinBuilder(String name){
        this.name = name;
        parameterInterface = new ComponentInterface();
        styles = new ArrayList<>();
    }

    public ComponentSkinBuilder withStyle(StyleClass style){
        styles.add(style);
        return this;
    }

    public ComponentSkinBuilder withParameter(String name, VarType type){
        parameterInterface.defineParameter(name, type);
        return this;
    }

    public ComponentSkin build(){
        return new ComponentSkin(name, parameterInterface, styles.toArray(new StyleClass[]{}));
    }

    public static ComponentSkinBuilder create(String name){
        return new ComponentSkinBuilder(name);
    }

}
