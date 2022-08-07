package myworld.obsidian.display.skin;

import java.util.Objects;

public record StyleRule(String name, Object... args) {

    @Override
    public boolean equals(Object other){
        if(other instanceof StyleRule rule){
            return name != null && name.equals(rule.name);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(name);
    }
}
