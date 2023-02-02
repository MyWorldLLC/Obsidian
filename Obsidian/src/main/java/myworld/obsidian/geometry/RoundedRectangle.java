package myworld.obsidian.geometry;

public record RoundedRectangle(Rectangle rectangle,
                               Distance leftRadius,
                               Distance topRadius,
                               Distance rightRadius,
                               Distance bottomRadius) {

    public static RoundedRectangle uniform(Rectangle rectangle, Distance radius){
        return new RoundedRectangle(rectangle, radius, radius, radius, radius);
    }

}
