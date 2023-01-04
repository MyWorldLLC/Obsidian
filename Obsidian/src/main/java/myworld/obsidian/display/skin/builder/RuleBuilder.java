package myworld.obsidian.display.skin.builder;

import myworld.obsidian.display.skin.StyleRule;

import java.util.HashMap;
import java.util.Map;

public class RuleBuilder {

    protected final Map<String, StyleRule> rules;

    protected RuleBuilder(){
        rules = new HashMap<>();
    }

    public RuleBuilder withRule(String name, StyleRule rule){
        rules.put(name, rule);
        return this;
    }

    public Map<String, StyleRule> build(){
        return rules;
    }

    public static RuleBuilder create(){
        return new RuleBuilder();
    }

}
