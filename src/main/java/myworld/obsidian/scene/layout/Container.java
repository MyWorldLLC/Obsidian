package myworld.obsidian.scene.layout;

import myworld.obsidian.geometry.Distance;
import myworld.obsidian.layout.FlexWrap;
import myworld.obsidian.layout.ItemAlignment;
import myworld.obsidian.layout.ItemJustification;
import myworld.obsidian.layout.Offsets;
import myworld.obsidian.scene.Component;

public class Container<T> extends Component {

    public Container(){
        this(false);
    }

    public Container(boolean focusable){
        this.focusable.set(focusable);
    }

    public Container<T> withPadding(Distance padding){
        layout.padding().set(new Offsets(padding));
        return this;
    }

    public Container<T> withPadding(Offsets padding){
        layout.padding().set(padding);
        return this;
    }

    public Container<T> withMargin(Distance margin){
        layout.margin().set(new Offsets(margin));
        return this;
    }

    public Container<T> withMargin(Offsets margin){
        layout.margin().set(margin);
        return this;
    }

    public Container<T> withWrapping(FlexWrap wrapping){
        layout.flexWrap().set(wrapping);
        return this;
    }

    public Container<T> withJustification(ItemJustification justification){
        layout.justifyContent().set(justification);
        return this;
    }

    public Container<T> withItemAlignment(ItemAlignment alignment){
        layout.alignItems().set(alignment);
        return this;
    }

    public Container<T> withContentAlignment(ItemAlignment alignment){
        layout.alignContent().set(alignment);
        return this;
    }

}
