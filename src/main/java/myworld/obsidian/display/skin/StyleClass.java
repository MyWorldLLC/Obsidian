package myworld.obsidian.display.skin;

public record StyleClass(String name, String layer, String stateParam, StyleRule... rules) {

    StyleClass(String name){
        this(name, null, null);
    }

    public boolean isLayer(){
        return layer != null;
    }

    public boolean isStateLimited(){
        return stateParam != null;
    }

}
