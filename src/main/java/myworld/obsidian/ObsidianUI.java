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

package myworld.obsidian;

import myworld.obsidian.scene.Component;
import myworld.obsidian.geometry.Dimension2D;
import myworld.obsidian.layout.LayoutEngine;
import myworld.obsidian.properties.ValueProperty;

public class ObsidianUI {

    protected final ValueProperty<Dimension2D> dimensions;
    protected final Component root;
    protected final LayoutEngine layout;

    public ObsidianUI(){
        root = new Component();
        dimensions = new ValueProperty<>(new Dimension2D(100, 100));
        layout = new LayoutEngine(this);
    }

    public Component getRoot(){
        return root;
    }

    public ValueProperty<Dimension2D> getDimensions(){
        return dimensions;
    }

    public void update(double tpf){
        layout.layout();
        updateEffects(root, tpf);
    }

    protected void updateEffects(Component component, double tpf){
        component.effects().forEach(e -> e.update(tpf));
        component.children().forEach(c -> updateEffects(c, tpf));
    }

}
