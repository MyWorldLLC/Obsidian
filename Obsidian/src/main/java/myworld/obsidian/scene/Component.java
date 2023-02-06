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

import myworld.obsidian.ObsidianUI;
import myworld.obsidian.display.RenderOrder;
import myworld.obsidian.display.skin.Variables;
import myworld.obsidian.events.dispatch.EventDispatcher;
import myworld.obsidian.geometry.Bounds2D;
import myworld.obsidian.geometry.Point2D;
import myworld.obsidian.layout.ComponentLayout;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.properties.MapProperty;
import myworld.obsidian.properties.ValueProperty;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Component {

    public static final String FOCUSED_DATA_NAME = "focused";
    public static final String HOVERED_DATA_NAME = "hovered";

    protected final ValueProperty<ObsidianUI> ui;
    protected final ValueProperty<Component> parent;
    protected final ListProperty<Component> children;
    protected final ListProperty<Effect> effects;
    protected final ListProperty<Object> tags;
    protected final ValueProperty<String> styleName;
    protected final ValueProperty<Boolean> focusable;
    protected final ValueProperty<Boolean> focused;
    protected final ValueProperty<Boolean> hoverable;
    protected final ValueProperty<Boolean> hovered;
    protected final ValueProperty<Boolean> layoutOnly;
    protected final ValueProperty<Boolean> clipChildren;
    protected final ValueProperty<RenderOrder> renderOrder;
    protected final MapProperty<String, Supplier<?>> renderVars;
    protected final ListProperty<Runnable> preLayouts;
    protected final ListProperty<Runnable> preRenderers;
    protected final EventDispatcher dispatcher;

    protected final ValueProperty<Boolean> needsUpdate;
    protected final ComponentLayout layout;

    public Component(Component... children){
        parent = new ValueProperty<>();
        ui = new DelegatingValueProperty<>(parent, Component::ui);
        this.children = new ListProperty<>(children);
        effects = new ListProperty<>();
        tags = new ListProperty<>();
        styleName = new ValueProperty<>(defaultStyleName(this));
        focusable = new ValueProperty<>(true);
        focused = new ValueProperty<>(false);
        hoverable = new ValueProperty<>(true);
        hovered = new ValueProperty<>(false);
        layoutOnly = new ValueProperty<>(false);
        clipChildren = new ValueProperty<>(false);
        renderOrder = new ValueProperty<>(RenderOrder.ASCENDING);
        renderVars = new MapProperty<>();
        preLayouts = new ListProperty<>();
        preRenderers = new ListProperty<>();
        dispatcher = new EventDispatcher();

        needsUpdate = new ValueProperty<>();

        layout = new ComponentLayout();

        renderVars.put(FOCUSED_DATA_NAME, focused);
        renderVars.put(HOVERED_DATA_NAME, hovered);
    }

    protected void requestVisualUpdate(){
        needsUpdate.set(true);
        if(hasParent()){
            getParent().requestVisualUpdate();
        }
    }

    public void addChild(Component child){
        if(child.hasParent()){
            throw new IllegalStateException("Component has already been added as a child");
        }
        child.parent().set(this);
        children.add(child);
        child.onAttach();
    }

    public void addChildren(Component... children){
        for(var child : children){
            addChild(child);
        }
    }

    public boolean removeChild(Component child){
        var removed = children.removeIf(c -> c == child);
        if(removed){
            child.parent().set(null);
            child.onDetach();
        }
        return removed;
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T withChildren(Component... children){
        addChildren(children);
        return (T) this;
    }

    public boolean isChild(Component child){
        return children.stream().anyMatch(c -> c == child);
    }

    public ValueProperty<ObsidianUI> ui(){
        return ui;
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

    public ListProperty<Effect> effects(){
        return effects;
    }

    public ListProperty<Object> tags(){
        return tags;
    }

    public ValueProperty<String> styleName(){
        return styleName;
    }

    public ValueProperty<Boolean> focusable(){
        return focusable;
    }

    public ValueProperty<Boolean> focused(){
        return focused;
    }

    public boolean isFocusable(){
        return focusable().get(false);
    }

    public ValueProperty<Boolean> hoverable(){
        return hoverable;
    }

    public ValueProperty<Boolean> hovered(){
        return hovered;
    }

    public boolean isHoverable(){
        return hoverable.get(false);
    }

    public ValueProperty<Boolean> layoutOnly(){
        return layoutOnly;
    }

    public boolean isLayoutOnly(){
        return layoutOnly.get();
    }

    public ValueProperty<Boolean> clipChildren(){
        return clipChildren;
    }

    public ValueProperty<RenderOrder> renderOrder(){
        return renderOrder;
    }

    public MapProperty<String, Supplier<?>> renderVars(){
        return renderVars;
    }

    public ListProperty<Runnable> preLayouts(){
        return preLayouts;
    }

    public void preLayout(Runnable r){
        preLayouts.add(r);
    }

    public ListProperty<Runnable> preRenderers(){
        return preRenderers;
    }

    public void preRender(Runnable r){
        preRenderers.add(r);
    }

    public Variables generateRenderVars(){
        var variables = new Variables();
        renderVars.forEach((k, s) -> {
            var v = s.get();
            if(v != null){
                variables.set(k, s.get());
            }
        });
        return variables;
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
                .filter(t -> type.isAssignableFrom(t.getClass()))
                .findFirst()
                .orElse(null);
    }

    public void disableFocus(){
        apply(c -> c.focusable().set(false));
    }

    public void enableFocus(){
        apply(c -> c.focusable().set(true));
    }

    public void onAttach(){}

    public void onDetach(){}

    public EventDispatcher dispatcher(){
        return dispatcher;
    }

    public Point2D localize(Point2D p){
        return localize(p.x(), p.y());
    }

    public Point2D localize(float x, float y){
        var bounds = ui.get().getLayout().getSceneBounds(this);
        return new Point2D(x - bounds.left(), y - bounds.top());
    }

    public float localizeX(float x){
        return localize(x, 0).x();
    }

    public float localizeY(float y){
        return localize(0, y).y();
    }

    public void addEffect(Effect effect){
        effects.add(effect);
    }

    public void removeEffect(Effect effect){
        effects.remove(effect);
    }

    public void apply(Consumer<Component> c){
        c.accept(this);
        children.forEach(child -> child.apply(c));
    }

    public Component with(Consumer<Component> c){
        c.accept(this);
        return this;
    }

    public Bounds2D getLocalBounds(){
        if(!ui.isSet()){
            throw new IllegalStateException("Component is not attached to a scene");
        }
        return ui.get().getLayout().getLocalBounds(this);
    }

    public Bounds2D getSceneBounds(){
        if(!ui.isSet()){
            throw new IllegalStateException("Component is not attached to a scene");
        }
        return ui.get().getLayout().getSceneBounds(this);
    }

    public static String defaultStyleName(Component component){
        return component.getClass().getSimpleName();
    }

    public static Component restyle(Component component, String componentStyleName){
        component.styleName().set(componentStyleName);
        return component;
    }

}
