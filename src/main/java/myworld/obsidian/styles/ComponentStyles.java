package myworld.obsidian.styles;

import myworld.obsidian.ObsidianPixels;
import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.geometry.Shape;
import myworld.obsidian.geometry.Stroke;
import myworld.obsidian.properties.ValueProperty;

public class ComponentStyles {

    protected final ValueProperty<Shape> shape;
    protected final ValueProperty<ColorRGBA> color;
    protected final ValueProperty<ObsidianPixels> image;
    protected final ValueProperty<Stroke> stroke;
    protected final ValueProperty<Double> strokeWidth;
    protected final ValueProperty<ColorRGBA> strokeColor;

    public ComponentStyles(){
        shape = new ValueProperty<>();
        color = new ValueProperty<>();
        image = new ValueProperty<>();
        stroke = new ValueProperty<>();
        strokeWidth = new ValueProperty<>();
        strokeColor = new ValueProperty<>();
    }
}
