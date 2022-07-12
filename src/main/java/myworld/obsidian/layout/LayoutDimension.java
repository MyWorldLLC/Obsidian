package myworld.obsidian.layout;

public record LayoutDimension(float value, Unit unit) {

    public static final LayoutDimension AUTO = new LayoutDimension(-1f, Unit.PIXELS);
    public static final LayoutDimension ZERO = new LayoutDimension(0f, Unit.PIXELS);

}
