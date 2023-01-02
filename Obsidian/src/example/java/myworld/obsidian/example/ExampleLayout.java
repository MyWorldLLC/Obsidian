package myworld.obsidian.example;

import myworld.obsidian.components.layout.Pane;
import myworld.obsidian.layout.Layout;
import myworld.obsidian.scene.Component;
import myworld.obsidian.scene.layout.Column;
import myworld.obsidian.scene.layout.Row;

public class ExampleLayout extends Pane {

    protected final Row primary;
    protected final Column left;
    protected final Column center;
    protected final Column right;

    public ExampleLayout(){

        primary = new Row();
        left = new Column();
        center = new Column();
        right = new Column();

        primary.layout().clampedSize(Layout.FULL_SIZE, Layout.FULL_SIZE);
        primary.layout().flexGrow().set(1f);
        left.layout().clampedSize(Layout.AUTO, Layout.FULL_SIZE);
        left.layout().flexGrow().set(1f);
        center.layout().clampedSize(Layout.AUTO, Layout.FULL_SIZE);
        center.layout().flexGrow().set(1f);
        right.layout().clampedSize(Layout.AUTO, Layout.FULL_SIZE);
        right.layout().flexGrow().set(1f);

        primary.addChildren(left, center, right);
        addChild(primary);
    }

    public Row primary(){
        return primary;
    }

    public Column left(){
        return left;
    }

    public Column center(){
        return center;
    }

    public Column right(){
        return right;
    }

}
