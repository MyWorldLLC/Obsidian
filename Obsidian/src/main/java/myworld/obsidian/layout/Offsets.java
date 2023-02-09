package myworld.obsidian.layout;

import myworld.obsidian.geometry.Distance;

import java.util.function.Function;

public record Offsets(Distance left, Distance top, Distance right, Distance bottom) {
    public static final Offsets AUTO = new Offsets(Layout.AUTO, Layout.AUTO, Layout.AUTO, Layout.AUTO);
    public static final Offsets ZERO = new Offsets(Layout.ZERO, Layout.ZERO, Layout.ZERO, Layout.ZERO);

    public Offsets(Distance dist){
        this(dist, dist, dist, dist);
    }

    public Offsets withLeft(Distance left){
        return new Offsets(left, top, right, bottom);
    }

    public Offsets withTop(Distance top){
        return new Offsets(left, top, right, bottom);
    }

    public Offsets withRight(Distance right){
        return new Offsets(left, top, right, bottom);
    }

    public Offsets withBottom(Distance bottom){
        return new Offsets(left, top, right, bottom);
    }

    public Offsets with(Function<Offsets, Offsets> modifier){
        return modifier.apply(this);
    }

    public static Offsets shift(Distance left, Distance top){
        return new Offsets(left, top, ZERO.right(), ZERO.bottom());
    }

    public static Offsets shift(float left, float top){
        return shift(Distance.pixels(left), Distance.pixels(top));
    }
}
