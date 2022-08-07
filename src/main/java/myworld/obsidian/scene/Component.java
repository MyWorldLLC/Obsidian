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

import myworld.obsidian.display.skin.Variables;
import myworld.obsidian.layout.ComponentLayout;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.properties.ValueProperty;
import myworld.obsidian.styles.ComponentStyles;

public class Component {

    protected final ValueProperty<Component> parent;
    protected final ListProperty<Component> children;
    protected final ListProperty<Effect> effects;
    protected final ListProperty<Object> tags;
    protected final Variables data;

    protected final ValueProperty<Boolean> needsUpdate;
    protected final ComponentLayout layout;
    protected final ComponentStyles styles;

    public Component(Component... children){
        parent = new ValueProperty<>();
        this.children = new ListProperty<>(children);
        effects = new ListProperty<>();
        tags = new ListProperty<>();
        data = new Variables();

        needsUpdate = new ValueProperty<>();

        layout = new ComponentLayout();
        styles = new ComponentStyles();
    }

    protected void requestVisualUpdate(){
        needsUpdate.set(true);
        if(hasParent()){
            getParent().requestVisualUpdate();
        }
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

    public Component getParent(){
        return parent.get();
    }

    public boolean hasParent(){
        return getParent() != null;
    }

    public ListProperty<Component> children(){
        return children;
    }

    public ComponentLayout layout(){
        return layout;
    }

    public ComponentStyles styles(){
        return styles;
    }

    public ListProperty<Effect> effects(){
        return effects;
    }

    public ListProperty<Object> tags(){
        return tags;
    }

    public Variables data(){
        return data;
    }

    public void tag(Object tag){
        removeTag(tag.getClass());
        tags.add(tag);
    }

    public boolean removeTag(Class<?> type){
        return tags.removeIf(t -> t.getClass().equals(type));
    }

    public boolean hasTag(Class<?> type){
        return getTag(type) != null;
    }

    @SuppressWarnings("unchecked")
    public <T> T getTag(Class<T> type){
        return (T) tags
                .stream()
                .filter(t -> type.isAssignableFrom(t.getClass())).findFirst()
                .orElse(null);
    }

    public void onAttach(){}

    public void onDetach(){}

}
