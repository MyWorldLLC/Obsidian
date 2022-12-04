package myworld.obsidian.display.skin;

public record StyleLookup(ComponentSkin component, UISkin ui) {

    public StyleClass getStyle(String name){
        var style = component.findNamed(name);
        if(style == null){
            style = ui.getStyle(name);
        }
        return style;
    }

}
