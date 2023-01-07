package myworld.obsidian.layout;

import myworld.obsidian.geometry.Distance;

public record Offsets(Distance top, Distance right, Distance bottom, Distance left) {
    public static final Offsets AUTO = new Offsets(Layout.AUTO, Layout.AUTO, Layout.AUTO, Layout.AUTO);
    public static final Offsets ZERO = new Offsets(Layout.ZERO, Layout.ZERO, Layout.ZERO, Layout.ZERO);

    public Offsets(Distance dist){
        this(dist, dist, dist, dist);
    }

    public static Offsets shift(Distance left, Distance top){
        return new Offsets(top, ZERO.right(), ZERO.bottom(), left);
    }

    public static Offsets shift(float left, float top){
        return shift(Distance.pixels(left), Distance.pixels(top));
    }
}
