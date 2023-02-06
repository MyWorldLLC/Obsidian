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

import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.display.Colors;
import myworld.obsidian.display.DisplayEngine;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.UISkin;
import myworld.obsidian.events.*;
import myworld.obsidian.events.input.*;
import myworld.obsidian.events.scene.FocusEvent;
import myworld.obsidian.geometry.Point2D;
import myworld.obsidian.input.ClipboardHandler;
import myworld.obsidian.input.InputManager;
import myworld.obsidian.input.Key;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.properties.MapProperty;
import myworld.obsidian.scene.Component;
import myworld.obsidian.layout.LayoutEngine;
import myworld.obsidian.properties.ValueProperty;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static myworld.obsidian.events.dispatch.EventFilters.mousePressed;
import static myworld.obsidian.events.dispatch.EventFilters.keyPressed;

public class ObsidianUI {

    public static final CursorHandler DISCARDING_CURSOR_HANDLER = (c) -> {};

    public static final String ROOT_COMPONENT_STYLE_NAME = "Root";

    protected final Component root;
    protected final ValueProperty<LayoutEngine> layout;
    protected final ValueProperty<DisplayEngine> display;
    protected final ValueProperty<InputManager> input;
    protected final ValueProperty<ClipboardHandler> clipboard;
    protected final ValueProperty<CursorHandler> cursor;

    protected final ValueProperty<ColorRGBA> clearColor;

    protected final ValueProperty<Component> focusedComponent;
    protected final ValueProperty<Component> hoveredComponent;
    protected final MapProperty<MouseButton, Component> buttonReceivers;

    protected final Map<String, UISkin> skins;
    protected final ValueProperty<String> selectedSkin;

    public static ObsidianUI createForGL(int width, int height, int samples, int framebufferHandle){
        return new ObsidianUI(DisplayEngine.createForGL(width, height, samples, framebufferHandle));
    }

    public static ObsidianUI createForCpu(int width, int height){
        return new ObsidianUI(DisplayEngine.createForCpu(width, height));
    }

    public static ObsidianUI createHeadless(){
        return new ObsidianUI(null);
    }

    public ObsidianUI(DisplayEngine display){
        root = new Component();
        root.styleName().set(ROOT_COMPONENT_STYLE_NAME);
        root.ui().set(this);
        root.dispatcher().subscribe(KeyEvent.class, keyPressed(Key.TAB), evt -> {
            if(evt.getManager().isShiftDown()){
                focusPrevious();
            }else{
                focusNext();
            }
        });
        root.dispatcher().addFilter(MouseButtonEvent.class, evt -> {
            if(mousePressed().test(evt)){
                var picked = pickFirst(evt.getX(), evt.getY(), Component::isFocusable);
                if(picked != null){
                    requestFocus(picked);
                }else{
                    unfocus();
                }
            }
            return true;
        });

        layout = new ValueProperty<>();
        layout.addListener((prop, oldValue, newValue) -> {
            if(oldValue != null){
                oldValue.unregisterRoot(root);
            }

            if(newValue != null){
                newValue.registerRoot(root);
            }
        });
        layout.set(new LayoutEngine(this));
        this.display = new ValueProperty<>(display);

        input = new ValueProperty<>(new InputManager(this));
        clipboard = new ValueProperty<>(ClipboardHandler.droppingClipboard());
        cursor = new ValueProperty<>(DISCARDING_CURSOR_HANDLER);

        focusedComponent = new ValueProperty<>();
        focusedComponent.addListener(this::focusedComponentChanged);
        hoveredComponent = new ValueProperty<>();
        hoveredComponent.addListener(this::hoveredComponentChanged);
        buttonReceivers = new MapProperty<>();

        skins = new ConcurrentHashMap<>();
        selectedSkin = new ValueProperty<>();

        clearColor = new ValueProperty<>(Colors.BLACK);

        // Register event handlers/filters that are useful for base UI functionality
        input.get().getDispatcher().subscribe(MouseMoveEvent.class, this::mouseHoverWatcher);
    }

    public LayoutEngine getLayout(){
        return layout.get();
    }

    public InputManager getInput(){
        return input.get();
    }

    public ValueProperty<DisplayEngine> display(){
        return display;
    }

    public DisplayEngine getDisplay(){
        return display.get();
    }

    public void setDisplay(DisplayEngine display){
        this.display.set(display);
    }

    public ValueProperty<ClipboardHandler> clipboard(){
        return clipboard;
    }

    public ValueProperty<CursorHandler> cursorHandler(){
        return cursor;
    }

    public ValueProperty<ColorRGBA> clearColor(){
        return clearColor;
    }

    public Component getRoot(){
        return root;
    }

    public void update(double tpf){
        layout.get().layout();
        updateEffects(root, tpf);
    }

    public void render(){
        display.ifSet(d -> {
            d.clear(clearColor.get());
            d.render(this, root, getSkin(selectedSkin.get()));
            d.flush();
        });
    }

    public void updateAndRender(double tpf){
        update(tpf);
        render();
    }

    protected void updateEffects(Component component, double tpf){
        component.effects().removeIf(e -> e.update(tpf));
        component.children().forEach(c -> updateEffects(c, tpf));
    }

    public void cleanup(){
        display.ifSet(DisplayEngine::close);
        layout.ifSet(engine -> engine.unregisterRoot(root));
    }

    public void registerSkin(UISkin skin){
        skins.put(skin.getName(), skin);
        display.ifSet(d -> d.loadResources(skin));
    }

    public UISkin getSkin(String name){
        return skins.get(name);
    }

    public void useSkin(String name){
        selectedSkin.set(name);
    }

    public ValueProperty<String> selectedSkin(){
        return selectedSkin;
    }

    public UISkin getSelectedSkin(){
        return getSkin(selectedSkin.get());
    }

    public StyleClass getStyle(String name){
        var skin = getSkin(selectedSkin.get());
        if(skin != null){
            return skin.getStyle(name);
        }
        return null;
    }

    public boolean requestFocus(Component component){
        if(component.isFocusable()){
            var oldFocus = focusedComponent.get();
            focusedComponent.set(component);
            fireEvent(new FocusEvent(oldFocus, component));
            return true;
        }
        return false;
    }

    public ValueProperty<Component> focusedComponent(){
        return focusedComponent;
    }

    public Component getFocusedComponent(){
        return focusedComponent.get();
    }

    public void unfocus(){
        focusedComponent.set(null);
    }

    public Component focusNext(){
        var search = focusedComponent.get();
        if(search == null){
            search = getRoot();
        }
        var found = traverseForFocusable(search, 0, search.children().size(), true);
        return found != null && requestFocus(found) ? found : null;
    }

    public Component focusPrevious(){
        var search = focusedComponent.get();
        if(search == null || search.getParent() == null){
            // If no component is focused (or the root is focused), we can't
            // focus on a "previous" component because the previous component
            // would come before this component in the parent's children
            return null;
        }
        var found = traverseForFocusable(search.getParent(), Math.max(0, search.getParent().children().indexOf(search) - 1), 0, false);
        return found != null && requestFocus(found) ? found : null;
    }

    protected Component traverseForFocusable(Component search, int start, int end, boolean searchForward){

        while(search != null){

            int step = searchForward ? 1 : -1;

            for(int i = start; searchForward ? i < end : i > end; i += step){
                var child = search.children().get(i);
                if(child.isFocusable()){
                    return child;
                }
                int nextStart = searchForward ? 0 : Math.max(0, child.children().size() - 1);
                int nextEnd = searchForward ? child.children().size() : 0;
                var found = traverseForFocusable(child, nextStart, nextEnd, searchForward);
                if(found != null){
                    return found;
                }
            }

            var current = search;
            search = search.getParent();
            if(search != null){ // TODO - without this, an NPE can occur, but this isn't quite right either
                var index = search.children().indexOf(current);
                start = searchForward ? index + 1 : Math.max(0, index - 1);
                end = searchForward ? search.children().size() : 0;
            }

        }
        return null;
    }

    protected void focusedComponentChanged(ValueProperty<Component> property, Component previous, Component next){
        if(previous != null){
            previous.focused().set(false);
        }
        if(next != null){
            next.focused().set(true);
        }
    }

    protected void hoveredComponentChanged(ValueProperty<Component> property, Component previous, Component next){
        if(previous != null){
            previous.hovered().set(false);
        }
        if(next != null){
            next.hovered().set(true);
        }
    }

    protected void mouseHoverWatcher(MouseMoveEvent evt){
        var current = pickFirst(evt.getX(), evt.getY(), Component::isHoverable);
        var former = hoveredComponent.get();
        if(current != former){
            hoveredComponent.set(current);
        }

        fireEvent(new MouseOverEvent(input.get(), evt.getX(), evt.getY(), former, current));
    }

    public Component pickFirst(int x, int y){
        return pickFirst(x, y, (c) -> true);
    }

    public Component pickFirst(int x, int y, Predicate<Component> pickable){
        var targets = pick(getRoot(), x, y, pickable);
        return targets.size() > 0 ? targets.get(0) : null;
    }

    public List<Component> pick(int x, int y){
        return pick(getRoot(), x, y);
    }

    public List<Component> pick(Component component, int x, int y){
        return pick(component, x, y, (c) -> true);
    }

    public List<Component> pick(int x, int y, Predicate<Component> pickable){
        return pick(getRoot(), x, y, pickable);
    }

    public List<Component> pick(Component component, int x, int y, Predicate<Component> pickable){
        var picked = new ArrayList<Component>();
        if(getLayout().testSceneBounds(component, x, y)){
            for(var child : component.children()){
                var candidates = pick(child, x, y, pickable);
                picked.addAll(candidates);
            }
            // Order this component after its child candidates, if it's pickable
            if(pickable.test(component)){
                picked.add(component);
            }
        }

        return picked;
    }

    public boolean isOver(Point2D p, Component c){
        return isOver((int)p.x(), (int)p.y(), c);
    }

    public boolean isOver(int x, int y, Component c){
        return pick(x, y).contains(c);
    }

    public void fireEvent(BaseEvent evt){
        fireEvent(evt, getRoot());
    }

    public void fireEvent(BaseEvent evt, Component eventRoot){

        /* Event dispatch happens in two distinct phases:
           (1) Target selection
           (2) Filter down & bubble up

           Target selection depends on the type of event:
           (1) Mouse events traverse the component hierarchy down and select the deepest component in the hierarchy
               that overlaps the mouse coordinates
           (2) Character events target the focused component if there is one, otherwise they are discarded
           (3) Focus/hover events events are dispatched at most twice - first targeting the component that has lost focus/hover
               (if there was one), then targeting the component that has gained focus/hover (if there is one)

           Filtering/bubbling happens identically regardless of the event type. First, a chain from the eventRoot to the
           target is constructed, and the chain is traversed from the root to the target, running any event filters registered
           for that event type. If any filter blocks the event, the event is discarded and is not dispatched to the target.
           If the event reaches the target, any event handlers registered for that event are run. The event is then dispatched
           at each component from the bottom of the chain to the top until consume() is called, at which point dispatch halts.
        */

        if(evt instanceof CharacterEvent characterEvent){
            var focused = getFocusedComponent();
            if(focused != null){
                dispatch(characterEvent, eventRoot, focused);
            }
        }else if(evt instanceof KeyEvent keyEvent){
            var focused = getFocusedComponent();
            if(focused != null){
                dispatch(keyEvent, eventRoot, focused);
            }
        }else if(evt instanceof FileDropEvent fileEvent){
            var focused = getFocusedComponent();
            if(focused != null){
                dispatch(fileEvent, eventRoot, focused);
            }
        }else if(evt instanceof FocusEvent focusEvent){
            if(focusEvent.getOldFocus() != null){
                dispatch(evt, eventRoot, focusEvent.getOldFocus());
            }

            if(focusEvent.getNewFocus() != null){
                dispatch(evt, eventRoot, focusEvent.getNewFocus());
            }
        }else if(evt instanceof MouseOverEvent hoverEvent){
            if(hoverEvent.getPrior() == hoverEvent.getCurrent()){
                dispatch(evt, eventRoot, hoverEvent.getCurrent());
            }else{
                if(hoverEvent.getPrior() != null){
                    dispatch(evt, eventRoot, hoverEvent.getPrior());
                }

                if(hoverEvent.getCurrent() != null){
                    dispatch(evt, eventRoot, hoverEvent.getCurrent());
                }
            }

        }else if(evt instanceof MouseButtonEvent buttonEvent){
            if(buttonEvent.isDown()){
                var mousePos = input.get().getMousePosition();
                var targets = mousePos != null ? pick(mousePos.x(), mousePos.y()) : List.of(eventRoot);
                dispatch(evt, eventRoot, targets,
                        (target) -> buttonReceivers.put(buttonEvent.getButton(), target));
            }else{
                var targets = new ArrayList<Component>();
                Component target = buttonReceivers.remove(buttonEvent.getButton());
                if(target != null){
                    targets.add(target);
                }
                var mousePos = input.get().getMousePosition();
                targets.addAll(mousePos != null ? pick(mousePos.x(), mousePos.y()) : List.of(eventRoot));
                dispatch(evt, eventRoot, targets);
            }

        }else{
            var mousePos = input.get().getMousePosition();
            List<Component> targets = new ArrayList<>(buttonReceivers.values());
            targets.addAll(mousePos != null ? pick(mousePos.x(), mousePos.y()) : List.of(eventRoot));
            if(targets.isEmpty()){
                targets = List.of(eventRoot);
            }
            dispatch(evt, eventRoot, targets);
        }
    }

    protected void dispatch(BaseEvent evt, Component root, List<Component> targets){
        dispatch(evt, root, targets, (target) -> {});
    }

    protected void dispatch(BaseEvent evt, Component root, List<Component> targets, Consumer<Component> onConsumed){
        // De-duplicate events to collections of receivers so that each receiver can only receive the event once.
        // There are multiple possible conditions whereby a receiver could be duplicated in a multi-dispatch list.
        var dedup = new HashSet<Component>();
        for(var target : targets){
            if(!dedup.add(target)){
                continue;
            }
            dispatch(evt, root, target);
            if(evt.isConsumed()) {
                onConsumed.accept(target);
                break;
            }
        }
    }

    protected void dispatch(BaseEvent evt, Component root, Component target){
        if(filter(evt, root, target)){
            bubble(evt, root, target);
        }
    }

    protected boolean filter(BaseEvent evt, Component root, Component component){

        if(component == null){
            return true;
        }

        if(evt.isConsumed()){
            return false; // Filter stage blocks the event as soon as it's consumed by a filter
        }

        var passed = true;
        if(component != root){
            // If we're not at the root yet, consider the parent's filter value and
            // only invoke our own filters if the parent (& ancestors') filters passed
            passed = filter(evt, root, component.getParent());
        }

        if(passed){
            // If all filters higher up in the chain passed, test this component's filters
            passed = component.dispatcher().filter(evt);
        }

        return passed;
    }

    protected void bubble(BaseEvent evt, Component root, Component component){
        if(component != null && !evt.isConsumed()){

            component.dispatcher().dispatch(evt);

            if(component != root && component.hasParent()){
                bubble(evt, root, component.getParent());
            }
        }
    }

}
