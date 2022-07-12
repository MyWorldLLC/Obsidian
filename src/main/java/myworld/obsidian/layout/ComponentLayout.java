package myworld.obsidian.layout;

import myworld.obsidian.properties.ValueProperty;

public class ComponentLayout {

    protected final ValueProperty<PositionType> positionType;
    protected final ValueProperty<Offsets> offsets;

    protected final ValueProperty<FlexDirection> flexDirection;
    protected final ValueProperty<FlexWrap> flexWrap;
    protected final ValueProperty<Float> flexGrow;
    protected final ValueProperty<Float> flexShrink;
    protected final ValueProperty<Float> flexBasis;

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

    public ComponentLayout(){
        positionType = new ValueProperty<>(PositionType.RELATIVE);
        offsets = new ValueProperty<>();

        flexDirection = new ValueProperty<>(FlexDirection.ROW);
        flexWrap = new ValueProperty<>(FlexWrap.NO_WRAP);
        flexGrow = new ValueProperty<>(0f);
        flexShrink = new ValueProperty<>(1f);
        flexBasis = new ValueProperty<>();

        alignContent = new ValueProperty<>();
        alignSelf = new ValueProperty<>(ItemAlignment.AUTO);
        alignItems = new ValueProperty<>(ItemAlignment.STRETCH);
        aspectRatio = new ValueProperty<>();

        justifyContent = new ValueProperty<>(ItemJustification.FLEX_START);
        layoutDirection = new ValueProperty<>(LayoutDirection.LTR);

        margin = new ValueProperty<>(Offsets.ZERO);
        padding = new ValueProperty<>(Offsets.ZERO);

        width = new ValueProperty<>(LayoutDimension.AUTO);
        height = new ValueProperty<>(LayoutDimension.AUTO);
    }

}
