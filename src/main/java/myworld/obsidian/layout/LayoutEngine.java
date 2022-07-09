/*
 *    Copyright 2022 MyWorld, LLC
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

package myworld.obsidian.layout;

import myworld.obsidian.ObsidianUI;
import myworld.obsidian.properties.ListChangeListener;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.scene.Component;

import static org.lwjgl.util.yoga.Yoga.*;

public class LayoutEngine {

    protected final ObsidianUI ui;

    protected final ListChangeListener<Component> listener;

    public LayoutEngine(ObsidianUI ui){
        this.ui = ui;
        listener = this::onComponentChange;
        ui.getRoot().children().addListener(this::onComponentChange);
    }

    public void enable(){
        ui.getRoot().children().addListener(listener);
    }

    public void disable(){
        ui.getRoot().children().removeListener(listener);
    }

    public void layout(){
        var yogaTag = ui.getRoot().getTag(YogaTag.class);
        if(yogaTag != null){
            var dimensions = ui.getDimensions().get();
            var fWidth = (float) dimensions.width();
            var fHeight = (float) dimensions.height();
            YGNodeCalculateLayout(yogaTag.node(), fWidth, fHeight, YGFlexDirectionRow);
        }
    }

    protected void onComponentChange(ListProperty<Component> children, int index, Component oldValue, Component newValue){
        if(oldValue == null && newValue != null){
            addLayout(newValue);
        }else if(oldValue != null && newValue == null){
            removeLayout(oldValue);
        }
    }

    protected void addLayout(Component component){
        component.children().addListener(listener);
        component.tag(new YogaTag(YGNodeNew()));
    }

    protected void removeLayout(Component component){
        component.children().removeListener(listener);
        var layout = component.getTag(YogaTag.class);
        if(layout != null){
            YGNodeFree(layout.node());
            component.removeTag(YogaTag.class);
        }
    }
}
