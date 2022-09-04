package myworld.obsidian.layout;

import myworld.obsidian.properties.ValueProperty;

public class ComponentLayout {

    protected final ValueProperty<Long> node;
    protected final ValueProperty<PositionType> positionType;
    protected final ValueProperty<Offsets> offsets;

    protected final ValueProperty<FlexDirection> flexDirection;
    protected final ValueProperty<FlexWrap> flexWrap;
    protected final ValueProperty<Float> flexGrow;
    protected final ValueProperty<Float> flexShrink;
    protected final ValueProperty<LayoutDimension> flexBasis;

    protected final ValueProperty<ItemAlignment> alignContent;
    protected final ValueProperty<ItemAlignment> alignSelf;
    protected final ValueProperty<ItemAlignment> alignItems;

    protected final ValueProperty<Float> aspectRatio;

    protected final ValueProperty<ItemJustification> justifyContent;
    protected final ValueProperty<LayoutDirection> layoutDirection;

    protected final ValueProperty<Offsets> margin;
    protected final ValueProperty<Offsets> padding;

    protected final ValueProperty<LayoutDimension> width;
    protected final ValueProperty<LayoutDimension> height;

    protected final ValueProperty<LayoutDimension> minWidth;
    protected final ValueProperty<LayoutDimension> maxWidth;

    protected final ValueProperty<LayoutDimension> minHeight;
    protected final ValueProperty<LayoutDimension> maxHeight;

    public ComponentLayout(){
        node = new ValueProperty<>();
        positionType = new ValueProperty<>(PositionType.RELATIVE);
        offsets = new ValueProperty<>();

        flexDirection = new ValueProperty<>(FlexDirection.ROW);
        flexWrap = new ValueProperty<>(FlexWrap.NO_WRAP);
        flexGrow = new ValueProperty<>(0f);
        flexShrink = new ValueProperty<>(1f);
        flexBasis = new ValueProperty<>(LayoutDimension.AUTO);

        alignContent = new ValueProperty<>(ItemAlignment.FLEX_START);
        alignSelf = new ValueProperty<>(ItemAlignment.AUTO);
        alignItems = new ValueProperty<>(ItemAlignment.STRETCH);

        aspectRatio = new ValueProperty<>(2f);

        justifyContent = new ValueProperty<>(ItemJustification.FLEX_START);
        layoutDirection = new ValueProperty<>(LayoutDirection.INHERIT);

        margin = new ValueProperty<>(Offsets.AUTO);
        padding = new ValueProperty<>(Offsets.ZERO);

        width = new ValueProperty<>(LayoutDimension.AUTO);
        height = new ValueProperty<>(LayoutDimension.AUTO);

        minWidth = new ValueProperty<>(LayoutDimension.AUTO);
        maxWidth = new ValueProperty<>(LayoutDimension.AUTO);

        minHeight = new ValueProperty<>(LayoutDimension.AUTO);
        maxHeight = new ValueProperty<>(LayoutDimension.AUTO);
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

    public ValueProperty<LayoutDimension> flexBasis() {
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

    public ValueProperty<LayoutDimension> preferredWidth() {
        return width;
    }

    public ValueProperty<LayoutDimension> preferredHeight() {
        return height;
    }

    public ValueProperty<LayoutDimension> minWidth(){
        return minWidth;
    }

    public ValueProperty<LayoutDimension> maxWidth(){
        return maxWidth;
    }

    public ValueProperty<LayoutDimension> minHeight(){
        return minHeight;
    }

    public ValueProperty<LayoutDimension> maxHeight(){
        return maxHeight;
    }

    public void minSize(LayoutDimension width, LayoutDimension height){
        minWidth.set(width);
        minHeight.set(height);
    }

    public void maxSize(LayoutDimension width, LayoutDimension height){
        maxWidth.set(width);
        maxHeight.set(height);
    }

    public void preferredSize(LayoutDimension width, LayoutDimension height){
        preferredWidth().set(width);
        preferredHeight().set(height);
    }

    public void clampedSize(LayoutDimension width, LayoutDimension height){
        minSize(width, height);
        preferredSize(width, height);
        maxSize(width, height);
    }
}
