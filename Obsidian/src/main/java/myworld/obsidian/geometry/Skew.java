package myworld.obsidian.geometry;

import java.util.Arrays;

public record Skew(Distance x, Distance y) {

    public static Skew of(Distance x, Distance y){
        return new Skew(x, y);
    }

    public static Skew uniform(Distance d){
        return new Skew(d, d);
    }

    public static Skew fromString(String s){
        var distances = Arrays.stream(s.split(","))
                .map(Distance::fromString).toArray(Distance[]::new);

        return switch (distances.length){
            case 1 -> Skew.uniform(distances[0]);
            case 2 -> Skew.of(distances[0], distances[1]);
            default -> throw new IllegalArgumentException("Skew string must consist of 1, or 2 distances");
        };
    }
}
