package myworld.obsidian.text;

public record Text(String text, TextSpan... spans) {

    public static Text create(String text, String styleClass){
        return new Text(text, TextSpan.fullSpan(text, styleClass));
    }

}
