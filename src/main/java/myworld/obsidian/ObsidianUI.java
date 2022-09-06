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
import myworld.obsidian.display.skin.UISkin;
import myworld.obsidian.scene.Component;
import myworld.obsidian.layout.LayoutEngine;
import myworld.obsidian.properties.ValueProperty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ObsidianUI {

    public static final String ROOT_COMPONENT_STYLE_NAME = "Root";

    protected final Component root;
    protected final ValueProperty<LayoutEngine> layout;
    protected final ValueProperty<DisplayEngine> display;
    protected final ValueProperty<ColorRGBA> clearColor;

    protected final Map<String, UISkin> skins;
    protected final ValueProperty<String> selectedSkin;

    public static ObsidianUI createForGL(int width, int height, int framebufferHandle){
        return new ObsidianUI(DisplayEngine.createForGL(width, height, framebufferHandle));
    }

    public static ObsidianUI createForCpu(int width, int height){
        return new ObsidianUI(DisplayEngine.createForCpu(width, height));
    }

    public ObsidianUI(DisplayEngine display){
        root = new Component();
        root.styleName().set(ROOT_COMPONENT_STYLE_NAME);
        layout = new ValueProperty<>();
        layout.addListener((prop, oldValue, newValue) -> {
            if(newValue != null){
                newValue.registerRoot(root);
            }
        });
        layout.set(new LayoutEngine(this));
        this.display = new ValueProperty<>(display);

        skins = new ConcurrentHashMap<>();
        selectedSkin = new ValueProperty<>();

        clearColor = new ValueProperty<>(Colors.BLACK);
    }

    public LayoutEngine getLayout(){
        return layout.get();
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
        component.effects().forEach(e -> e.update(tpf));
        component.children().forEach(c -> updateEffects(c, tpf));
    }

    public void cleanup(){
        display.ifSet(DisplayEngine::close);
        layout.ifSet(engine -> engine.unregisterRoot(root));
    }


    public void registerSkin(UISkin skin){
        skins.put(skin.getName(), skin);
    }

    public UISkin getSkin(String name){
        return skins.get(name);
    }

    public void useSkin(String name){
        selectedSkin.set(name);
    }

}
