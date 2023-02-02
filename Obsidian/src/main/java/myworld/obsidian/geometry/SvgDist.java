package myworld.obsidian.geometry;

public record SvgDist(Direction direction, Distance distance) {

    public enum Direction {
        HORIZONTAL, VERTICAL
    }

    public static SvgDist horizontal(Distance d){
        return new SvgDist(Direction.HORIZONTAL, d);
    }

    public static SvgDist vertical(Distance d){
        return new SvgDist(Direction.VERTICAL, d);
    }
}
