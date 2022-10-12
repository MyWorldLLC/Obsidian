package myworld.obsidian.text;

public record Text(String text, String styleClass) {

    public static Text plain(String text){
        return new Text(text, null);
    }

    public static Text styled(String text, String styleClass){
        return new Text(text, styleClass);
    }

    public boolean hasStyle(){
        return styleClass != null && !styleClass.isEmpty();
    }
}
