package myworld.obsidian.layout;

import myworld.obsidian.geometry.Distance;

import java.util.Arrays;
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

    public static Offsets uniform(Distance d){
        return new Offsets(d);
    }

    public static Offsets shift(Distance left, Distance top){
        return new Offsets(left, top, ZERO.right(), ZERO.bottom());
    }

    public static Offsets shift(float left, float top){
        return shift(Distance.pixels(left), Distance.pixels(top));
    }

    public static Offsets fromString(String offsets){
        var distances = Arrays.stream(offsets.split(","))
                .map(Distance::fromString).toArray(Distance[]::new);

        if(distances.length == 1){
            return Offsets.uniform(distances[0]);
        }else if(distances.length == 2){
            return Offsets.shift(distances[0], distances[1]);
        }else if(distances.length == 4){
            return new Offsets(distances[0], distances[1], distances[2], distances[3]);
        }

        throw new IllegalArgumentException("Offset string must consist of 1, 2, or 4 distances");
    }
}
