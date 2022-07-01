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

package myworld.obsidian.scene;

import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.properties.ValueProperty;

public class Component {

    protected final ValueProperty<Component> parent;
    protected final ListProperty<Component> children;
    protected final ListProperty<Object> styles;
    protected final ListProperty<Effect> effects;

    public Component(Component... children){
        parent = new ValueProperty<>();
        this.children = new ListProperty<>(children);
        styles = new ListProperty<>();
        effects = new ListProperty<>();
    }

    public void addChild(Component child){
        if(isChild(child)){
            throw new IllegalStateException("Component has already been added as a child");
        }
        children.add(child);
        child.parent().set(this);
        child.onAttach();
    }

    public boolean removeChild(Component child){
        var removed = children.removeIf(c -> c == child);
        if(removed){
            child.parent().set(null);
            child.onDetach();
        }
        return removed;
    }

    public boolean isChild(Component child){
        return children.stream().anyMatch(c -> c == child);
    }

    public ValueProperty<Component> parent(){
        return parent;
    }

    public ListProperty<Component> children(){
        return children;
    }

    public ListProperty<Object> styles(){
        return styles;
    }

    public ListProperty<Effect> effects(){
        return effects;
    }

    public void onAttach(){}

    public void onDetach(){}

}
