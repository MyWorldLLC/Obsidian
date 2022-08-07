package myworld.obsidian.display.skin;

import java.util.Arrays;
import java.util.stream.Collectors;

public record StyleClass(String name, String layer, String stateParam, StyleRule... rules) {

    public StyleClass(String name){
        this(name, null, null);
    }

    public StyleClass(StyleRule... rules){
        this("", null, null, rules);
    }

    public boolean isLayer(){
        return layer != null;
    }

    public boolean isStateLimited(){
        return stateParam != null;
    }

    public static StyleClass merge(StyleClass... classes){
        return new StyleClass(Arrays.stream(classes)
                .flatMap(style -> Arrays.stream(style.rules()))
                .collect(Collectors.toSet()).toArray(new StyleRule[]{}));
    }

}
