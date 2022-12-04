package myworld.obsidian.display.skin;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public record StyleClass(String name, String layer, boolean isForeground, String stateParam, Map<String, StyleRule> rules) {

    public StyleClass {
        Objects.requireNonNull(rules, "Style class rules may not be null");
        rules = Map.copyOf(rules);
    }

    public StyleClass(String name, String layer, String stateParam, Map<String, StyleRule> rules){
        this(name, layer, false, stateParam, rules);
    }

    public StyleClass(Map<String, StyleRule> rules){
        this(null, null, false, null, rules);
    }

    public StyleClass(){
        this(null, null, false, null, Collections.emptyMap());
    }

    public boolean isLayer(){
        return layer != null;
    }

    public boolean isForegroundLayer(){
        return isLayer() && isForeground;
    }

    public boolean isStateLimited(){
        return stateParam != null;
    }

    public <T> T rule(String name, Variables data){
        return rule(name).evaluate(data);
    }

    public <T> T rule(String name, Variables data, T defaultValue){
        return ruleOrConstant(name, defaultValue).evaluate(data);
    }

    @SuppressWarnings("unchecked")
    public StyleRule rule(String name){
        return ruleOrConstant(name, null);
    }

    public <T> StyleRule ruleOrConstant(String name, T defaultValue){
        StyleRule rule = rules.get(name);
        return rule != null ? rule : new ConstantRule<>(defaultValue);
    }

    public boolean hasRule(String name){
        return rule(name) != null;
    }

    public boolean hasAny(String... names){
        for(var name : names){
            if(hasRule(name)){
                return true;
            }
        }
        return false;
    }

    public static StyleClass merge(Collection<StyleClass> classes){
        return merge(classes.stream());
    }

    public static StyleClass merge(StyleClass... classes){
        return merge(Arrays.stream(classes));
    }

    public static StyleClass merge(Stream<StyleClass> classes){
        return new StyleClass(classes.flatMap(style -> style.rules().entrySet().stream())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (existing, next) -> next)));
    }

    public static StyleClass forName(String name, Map<String, StyleRule> rules){
        return new StyleClass(name, null, null, rules);
    }

    public static StyleClass forLayer(String layer, Map<String, StyleRule> rules){
        return new StyleClass(null, layer, null, rules);
    }

    public static StyleClass forForegroundLayer(String layer, Map<String, StyleRule> rules){
        return new StyleClass(null, layer, true,null, rules);
    }

    public static StyleClass forState(String stateParam, Map<String, StyleRule> rules){
        return new StyleClass(null, null, stateParam, rules);
    }

    public static StyleClass forLayerState(String layer, String stateParam, Map<String, StyleRule> rules){
        return new StyleClass(null, layer, stateParam, rules);
    }

    public static StyleClass forForegroundLayerState(String layer, String stateParam, Map<String, StyleRule> rules){
        return new StyleClass(null, layer, true, stateParam, rules);
    }

    public static Map<String, StyleRule> normalize(Map<String, Object> style){
        var rules = new HashMap<String, StyleRule>(style.size());
        style.forEach((k, v) -> {
            rules.put(k, wrap(v));
        });
        return rules;
    }

    public static StyleRule wrap(Object ruleOrValue){
        if(ruleOrValue instanceof StyleRule rule){
            return rule;
        }else{
            return new ConstantRule<>(ruleOrValue);
        }
    }

    public static <T> T evaluate(Object ruleOrValue, Variables v){
        return wrap(ruleOrValue).evaluate(v);
    }

}
