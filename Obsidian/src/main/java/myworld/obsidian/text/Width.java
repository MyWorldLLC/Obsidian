package myworld.obsidian.text;

public enum Width {

    ULTRA_CONDENSED(1),
    EXTRA_CONDENSED(2),
    CONDENSED(3),
    SEMI_CONDENSED(4),
    NORMAL(5),
    SEMI_EXPANDED(6),
    EXPANDED(7),
    EXTRA_EXPANDED(8),
    ULTRA_EXPANDED(9);

    private final int value;

    Width(int value){
        this.value = value;
    }

    public int getWidth(){
        return value;
    }
}
