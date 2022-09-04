package myworld.obsidian.layout;

public record LayoutDimension(float value, Unit unit) {

    public static final LayoutDimension AUTO = new LayoutDimension(-1f, Unit.PIXELS);
    public static final LayoutDimension ZERO = new LayoutDimension(0f, Unit.PIXELS);

    public static LayoutDimension of(float value, Unit unit){
        return new LayoutDimension(value, unit);
    }

    public static LayoutDimension pixels(float value){
        return of(value, Unit.PIXELS);
    }

    public static LayoutDimension percentage(float value){
        return of(value, Unit.PERCENTAGE);
    }

}
