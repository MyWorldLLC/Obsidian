package myworld.obsidian.text;

public record TextSpan(int from, int to, String styleClass) {

    public static TextSpan fullSpan(String text, String styleClass){
        return new TextSpan(0, text.length(), styleClass);
    }

}
