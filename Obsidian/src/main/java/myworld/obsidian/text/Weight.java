package myworld.obsidian.text;

public enum Weight {
    INVISIBLE(0),
    THIN(100),
    EXTRA_LIGHT(200),
    LIGHT(300),
    NORMAL(400),
    MEDIUM(500),
    SEMI_BOLD(600),
    BOLD(700),
    EXTRA_BOLD(800),
    BLACK(900),
    EXTRA_BLACK(1000);

    private final int value;

    Weight(int value){
        this.value = value;
    }

    public int getWeight(){
        return value;
    }
}
