/*
 *    Copyright 2023 MyWorld, LLC
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package myworld.obsidian.components.layout;

import myworld.obsidian.display.RenderOrder;
import myworld.obsidian.layout.Offsets;
import myworld.obsidian.layout.PositionType;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.scene.Component;


/**
 * Unskinned layout component that sets its childrens' layout sizes
 * to its own, with children laid out front-to-back.
 */
public class Stack extends Component {

    public Stack(){
        layoutOnly.set(true);
        focusable.set(false);
        renderOrder.set(RenderOrder.DESCENDING);

        effects.add(tpf -> {
            ui.ifSet(ui -> {
               var size = ui.getLayout().getSceneBounds(this);
               children.forEach(child -> {
                   if(!ui.getLayout().getSceneBounds(child).equals(size)){
                       child.layout().clampedSize(size.width(), size.height());
                   }
               });
            });
            return false;
        });

        children.addListener(this::childListener);
    }

    protected void childListener(ListProperty<Component> prop, int index, Component oldValue, Component newValue){
        if(ListProperty.isAdd(oldValue, newValue)){
            newValue.layout().positionType().set(PositionType.ABSOLUTE);
            newValue.layout().offsets().set(Offsets.ZERO);
        }
    }

}
