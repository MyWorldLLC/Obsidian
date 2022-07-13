package myworld.obsidian.layout;

public record Offsets(LayoutDimension top, LayoutDimension right, LayoutDimension bottom, LayoutDimension left) {
    public static final Offsets AUTO = new Offsets(LayoutDimension.AUTO, LayoutDimension.AUTO, LayoutDimension.AUTO, LayoutDimension.AUTO);
    public static final Offsets ZERO = new Offsets(LayoutDimension.ZERO, LayoutDimension.ZERO, LayoutDimension.ZERO, LayoutDimension.ZERO);
}
