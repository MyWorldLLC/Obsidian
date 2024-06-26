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
import myworld.obsidian.scene.events.AttachEvent;
import myworld.obsidian.scene.events.DetachEvent;
import myworld.obsidian.util.ReverseListIterator;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static myworld.obsidian.display.RenderOrder.ASCENDING;

public class Component {

    public static final String FOCUSED_DATA_NAME = "focused";
    public static final String HOVERED_DATA_NAME = "hovered";

    protected final ValueProperty<ObsidianUI> ui;
    protected final ValueProperty<String> id;
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
    protected final ListProperty<Runnable> postLayouts;
    protected final ListProperty<Runnable> preRenderers;
    protected final EventDispatcher dispatcher;

    protected final ValueProperty<Boolean> needsUpdate;
    protected final ComponentLayout layout;

    public Component(Component... children){
        parent = new ValueProperty<>();
        ui = new DelegatingValueProperty<>(parent, Component::ui);
        id = new ValueProperty<>();
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
        renderOrder = new ValueProperty<>(ASCENDING);
        renderVars = new MapProperty<>();
        preLayouts = new ListProperty<>();
        postLayouts = new ListProperty<>();
        preRenderers = new ListProperty<>();
        dispatcher = new EventDispatcher();

        needsUpdate = new ValueProperty<>();

        layout = new ComponentLayout();

        renderVars.put(FOCUSED_DATA_NAME, focused);
        renderVars.put(HOVERED_DATA_NAME, hovered);

        parent.addListener((p, o, n) -> {
            if(o != null && n == null){
                onDetach(o);
            }else if(o == null && n != null){
                onAttach(n);
            }
        });
    }

    protected void requestVisualUpdate(){
        needsUpdate.set(true);
        if(hasParent()){
            getParent().requestVisualUpdate();
        }
    }

    public Optional<Component> find(Predicate<Component> p){

        if(p.test(this)){
            return Optional.of(this);
        }

        for(var child : children){
            var maybeMatch = child.find(p);
            if(maybeMatch.isPresent()){
                return maybeMatch;
            }
        }

        return Optional.empty();
    }

    public Optional<Component> findDirectChild(Predicate<Component> p){
        return children.stream().filter(p).findFirst();
    }

    public Optional<Component> find(String id){

        var component = this;

        for(var partial : id.split("\\.")){
            var found = false;

            for(var child : component.children()){
                if(child.id().is(partial)){
                    component = child;
                    found = true;
                    break;
                }
            }

            if(!found){
                return Optional.empty();
            }
        }

        return Optional.of(component);
    }

    public void addChild(Component child){
        addChild(children.size(), child);
    }

    public void addChild(int index, Component child){
        if(child.hasParent()){
            throw new IllegalStateException("Component has already been added as a child");
        }
        child.parent().set(this);
        children.add(index, child);
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
        }
        return removed;
    }

    public void removeChildren(Component... children){
        for(var child : children){
            removeChild(child);
        }
    }

    public void removeAllChildren(){
        removeChildren(children.toArray(new Component[]{}));
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

    public ValueProperty<String> id(){
        return id;
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

    public void removeFromParent(){
        var parent = getParent();
        if(parent != null){
            parent.removeChild(this);
        }
    }

    public ListProperty<Component> children(){
        return children;
    }

    public Iterable<Component> childrenInRenderedOrder(){
        return switch (renderOrder.get()){
            case ASCENDING -> children;
            case DESCENDING -> () -> new ReverseListIterator<>(children.listIterator(children.size()));
        };
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

    public ListProperty<Runnable> postLayouts() {
        return postLayouts;
    }

    public void postLayout(Runnable r){
        postLayouts.add(r);
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

    private final void onAttach(Component parent){
        dispatcher.dispatch(new AttachEvent(parent, this));
    }

    private final void onDetach(Component parent){
        dispatcher.dispatch(new DetachEvent(parent, this));
    }

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
        // Use index-based iteration so we don't get
        // ConcurrentModificationException if modifying
        // a component during apply().
        for(int i = 0; i < children.size(); i++){
            children.get(i).apply(c);
        }
    }

    public <T> Stream<T> calculate(Function<Component, T> f){
        var t = f.apply(this);
        var streams = new ArrayList<Stream<T>>();

        // Again, index-based iteration so we don't have an
        // issue if a function modifies a component's children
        // during calculate().
        for(int i = 0; i < children.size(); i++){
            streams.add(children.get(i).calculate(f));
        }

        return Stream.concat(Stream.of(t), streams.stream().flatMap(Function.identity()));
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

    public Bounds2D getContentBounds(){
        if(children.isEmpty()){
            return getSceneBounds();
        }else{
            var bounds = children.stream().map(Component::getSceneBounds).toList();
            return bounds.get(0).merge(bounds.subList(1, bounds.size()).toArray(new Bounds2D[]{}));
        }
    }

    @Override
    public String toString(){
        var baseString = super.toString();
        return id().isSet() ? "%s[%s]".formatted(baseString, id().get()) : baseString;
    }

    public static String defaultStyleName(Component component){
        return component.getClass().getSimpleName();
    }

    public static Component restyle(Component component, String componentStyleName){
        component.styleName().set(componentStyleName);
        return component;
    }

}
