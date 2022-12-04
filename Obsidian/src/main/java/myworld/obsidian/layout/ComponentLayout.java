package myworld.obsidian.layout;

import myworld.obsidian.geometry.Distance;
import myworld.obsidian.properties.ValueProperty;

public class ComponentLayout {

    protected final ValueProperty<Long> node;
    protected final ValueProperty<PositionType> positionType;
    protected final ValueProperty<Offsets> offsets;

    protected final ValueProperty<FlexDirection> flexDirection;
    protected final ValueProperty<FlexWrap> flexWrap;
    protected final ValueProperty<Float> flexGrow;
    protected final ValueProperty<Float> flexShrink;
    protected final ValueProperty<Distance> flexBasis;

    protected final ValueProperty<ItemAlignment> alignContent;
    protected final ValueProperty<ItemAlignment> alignSelf;
    protected final ValueProperty<ItemAlignment> alignItems;

    protected final ValueProperty<Float> aspectRatio;

    protected final ValueProperty<ItemJustification> justifyContent;
    protected final ValueProperty<LayoutDirection> layoutDirection;

    protected final ValueProperty<Offsets> margin;
    protected final ValueProperty<Offsets> padding;

    protected final ValueProperty<Distance> width;
    protected final ValueProperty<Distance> height;

    protected final ValueProperty<Distance> minWidth;
    protected final ValueProperty<Distance> maxWidth;

    protected final ValueProperty<Distance> minHeight;
    protected final ValueProperty<Distance> maxHeight;

    public ComponentLayout(){
        node = new ValueProperty<>();
        positionType = new ValueProperty<>(PositionType.RELATIVE);
        offsets = new ValueProperty<>();

        flexDirection = new ValueProperty<>(FlexDirection.ROW);
        flexWrap = new ValueProperty<>(FlexWrap.NO_WRAP);
        flexGrow = new ValueProperty<>(0f);
        flexShrink = new ValueProperty<>(1f);
        flexBasis = new ValueProperty<>(Layout.AUTO);

        alignContent = new ValueProperty<>(ItemAlignment.FLEX_START);
        alignSelf = new ValueProperty<>(ItemAlignment.AUTO);
        alignItems = new ValueProperty<>(ItemAlignment.STRETCH);

        aspectRatio = new ValueProperty<>(Layout.UNDEFINED);

        justifyContent = new ValueProperty<>(ItemJustification.FLEX_START);
        layoutDirection = new ValueProperty<>(LayoutDirection.INHERIT);

        margin = new ValueProperty<>(Offsets.AUTO);
        padding = new ValueProperty<>(Offsets.ZERO);

        width = new ValueProperty<>(Layout.AUTO);
        height = new ValueProperty<>(Layout.AUTO);

        minWidth = new ValueProperty<>(Layout.AUTO);
        maxWidth = new ValueProperty<>(Layout.AUTO);

        minHeight = new ValueProperty<>(Layout.AUTO);
        maxHeight = new ValueProperty<>(Layout.AUTO);
    }

    public ValueProperty<Long> node(){
        return node;
    }

    public ValueProperty<PositionType> positionType() {
        return positionType;
    }

    public ValueProperty<Offsets> offsets() {
        return offsets;
    }

    public ValueProperty<FlexDirection> flexDirection() {
        return flexDirection;
    }

    public ValueProperty<FlexWrap> flexWrap() {
        return flexWrap;
    }

    public ValueProperty<Float> flexGrow() {
        return flexGrow;
    }

    public ValueProperty<Float> flexShrink() {
        return flexShrink;
    }

    public ValueProperty<Distance> flexBasis() {
        return flexBasis;
    }

    public ValueProperty<ItemAlignment> alignContent() {
        return alignContent;
    }

    public ValueProperty<ItemAlignment> alignSelf() {
        return alignSelf;
    }

    public ValueProperty<ItemAlignment> alignItems() {
        return alignItems;
    }

    public ValueProperty<Float> aspectRatio() {
        return aspectRatio;
    }

    public ValueProperty<ItemJustification> justifyContent() {
        return justifyContent;
    }

    public ValueProperty<LayoutDirection> layoutDirection() {
        return layoutDirection;
    }

    public ValueProperty<Offsets> margin() {
        return margin;
    }

    public ValueProperty<Offsets> padding() {
        return padding;
    }

    public ValueProperty<Distance> preferredWidth() {
        return width;
    }

    public ValueProperty<Distance> preferredHeight() {
        return height;
    }

    public ValueProperty<Distance> minWidth(){
        return minWidth;
    }

    public ValueProperty<Distance> maxWidth(){
        return maxWidth;
    }

    public ValueProperty<Distance> minHeight(){
        return minHeight;
    }

    public ValueProperty<Distance> maxHeight(){
        return maxHeight;
    }

    public void minSize(Distance width, Distance height){
        minWidth.set(width);
        minHeight.set(height);
    }

    public void maxSize(Distance width, Distance height){
        maxWidth.set(width);
        maxHeight.set(height);
    }

    public void preferredSize(Distance width, Distance height){
        preferredWidth().set(width);
        preferredHeight().set(height);
    }

    public void clampedSize(Distance width, Distance height){
        minSize(width, height);
        preferredSize(width, height);
        maxSize(width, height);
    }
}
