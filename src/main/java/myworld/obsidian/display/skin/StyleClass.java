package myworld.obsidian.display.skin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record StyleClass(String name, String layer, String stateParam, Map<String, Object> rules) {

    public StyleClass {
        Objects.requireNonNull(rules, "Style class rules may not be null");
        rules = Map.copyOf(rules);
    }

    public StyleClass(String name){
        this(name, null, null, Collections.emptyMap());
    }

    public StyleClass(Map<String, Object> rules){
        this(null, null, null, rules);
    }

    public boolean isLayer(){
        return layer != null;
    }

    public boolean isStateLimited(){
        return stateParam != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T rule(String name){
        return (T) rules.get(name);
    }

    public <T> T rule(String name, T defaultValue){
        T value = rule(name);
        return value != null ? value : defaultValue;
    }

    public static StyleClass merge(Collection<StyleClass> classes){
        return merge(classes.stream());
    }

    public static StyleClass merge(StyleClass... classes){
        return merge(Arrays.stream(classes));
    }

    public static StyleClass merge(Stream<StyleClass> classes){
        return new StyleClass(classes.flatMap(style -> style.rules().entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public static StyleClass forName(String name, Map<String, Object> rules){
        return new StyleClass(name, null, null, rules);
    }

    public static StyleClass forLayer(String layer, Map<String, Object> rules){
        return new StyleClass(null, layer, null, rules);
    }

    public static StyleClass forState(String stateParam, Map<String, Object> rules){
        return new StyleClass(null, null, stateParam, rules);
    }

    public static StyleClass forLayerState(String layer, String stateParam, Map<String, Object> rules){
        return new StyleClass(null, layer, stateParam, rules);
    }

}
