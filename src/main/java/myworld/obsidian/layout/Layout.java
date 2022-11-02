package myworld.obsidian.layout;

import myworld.obsidian.geometry.Distance;
import myworld.obsidian.geometry.Unit;

import static org.lwjgl.util.yoga.Yoga.YGUndefined;

public class Layout {

    public static final float UNDEFINED = YGUndefined;

    public static final Distance AUTO = new Distance(-1f, Unit.PIXELS);
    public static final Distance ZERO = new Distance(0f, Unit.PIXELS);

    public static Distance FULL_SIZE = Distance.percentage(100);
    public static Distance HALF_SIZE = Distance.percentage(50);
    public static Distance THIRD_SIZE = Distance.percentage(33);
    public static Distance QUARTER_SIZE = Distance.percentage(25);
}
