package myworld.obsidian.display.skin;

import java.util.Objects;

public record StyleRule(String name, Object... args) {

    public <T> T arg(int index, T defaultValue){
        T value = arg(index);
        return value == null ? defaultValue : value;
    }

    @SuppressWarnings("unchecked")
    public <T> T arg(int index){
        return (T) args[index];
    }

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
