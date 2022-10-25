package myworld.obsidian.text;

import myworld.obsidian.display.skin.StyleClass;

public record Text(String text, StyleClass style) {

    public static Text plain(String text){
        return new Text(text, null);
    }

    public static Text styled(String text, StyleClass style){
        return new Text(text, style);
    }

    public boolean hasStyle(){
        return style != null;
    }
}
