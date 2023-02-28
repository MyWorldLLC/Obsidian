package myworld.obsidian.text;

public enum TextStyle {
    BOLD,
    ITALIC,
    BOLD_ITALIC,
    NORMAL;

    public static TextStyle named(String style){
        return switch (style){
            case "BOLD" -> BOLD;
            case "ITALIC" -> ITALIC;
            case "BOLD_ITALIC" -> BOLD_ITALIC;
            case "NORMAL" -> NORMAL;
            default -> throw new IllegalArgumentException("Invalid text style name: " + style);
        };
    }
}
