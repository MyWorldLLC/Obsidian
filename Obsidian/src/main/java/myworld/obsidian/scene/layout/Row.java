package myworld.obsidian.scene.layout;

import myworld.obsidian.layout.FlexDirection;
import myworld.obsidian.layout.ItemAlignment;
import myworld.obsidian.layout.ItemJustification;

public class Row extends Container<Row> {

    public Row(){
        this(true);
    }

    public Row(boolean leftToRight){
        layout.flexDirection().set(leftToRight ? FlexDirection.ROW : FlexDirection.ROW_REVERSE);
        withItemAlignment(ItemAlignment.CENTER);
        withJustification(ItemJustification.SPACE_AROUND);
        layoutOnly().set(true);
    }

}
