package myworld.obsidian.display.skin;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public StyleRule rule(String name){
        return rule(name, null);
    }

    public StyleRule rule(String name, StyleRule defaultRule){
        return Arrays.stream(rules)
                .filter(r -> r.name().equals(name))
                .findFirst()
                .orElse(defaultRule);
    }

    public static StyleClass merge(Collection<StyleClass> classes){
        return merge(classes.stream());
    }

    public static StyleClass merge(StyleClass... classes){
        return merge(Arrays.stream(classes));
    }

    public static StyleClass merge(Stream<StyleClass> classes){
        return new StyleClass(classes.flatMap(style -> Arrays.stream(style.rules()))
                .collect(Collectors.toSet()).toArray(new StyleRule[]{}));
    }

}
