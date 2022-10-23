package myworld.obsidian.layout;

import myworld.obsidian.geometry.Distance;

public record Offsets(Distance top, Distance right, Distance bottom, Distance left) {
    public static final Offsets AUTO = new Offsets(LayoutDimension.AUTO, LayoutDimension.AUTO, LayoutDimension.AUTO, LayoutDimension.AUTO);
    public static final Offsets ZERO = new Offsets(LayoutDimension.ZERO, LayoutDimension.ZERO, LayoutDimension.ZERO, LayoutDimension.ZERO);
}
