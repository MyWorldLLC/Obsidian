package myworld.obsidian.scene.layout;

import myworld.obsidian.layout.FlexDirection;
import myworld.obsidian.layout.ItemAlignment;
import myworld.obsidian.layout.ItemJustification;

public class Column extends Container<Column> {

    public Column(){
        this(true);
    }

    public Column(boolean topToBottom){
        layout.flexDirection().set(topToBottom ? FlexDirection.COLUMN : FlexDirection.COLUMN_REVERSE);
        withItemAlignment(ItemAlignment.CENTER);
        withJustification(ItemJustification.FLEX_START);
        layoutOnly().set(true);
    }
}
