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

package myworld.obsidian.display.skin.chipmunk;

import chipmunk.runtime.ChipmunkModule;
import chipmunk.vm.invoke.security.AllowChipmunkLinkage;
import myworld.obsidian.display.skin.ComponentInterface;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.StyleRule;
import myworld.obsidian.display.skin.VarType;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;
import java.util.Map;

import static myworld.obsidian.display.skin.StyleClass.evaluate;

public class ComponentModule implements ChipmunkModule {

    protected static final Logger log = Logger.getLogger(ComponentModule.class.getName());

    public static final String MODULE_NAME = "obsidian.component";

    protected final List<StyleClass> styles = new ArrayList<>();

    protected String name;
    protected final ComponentInterface componentInterface = new ComponentInterface();

    @AllowChipmunkLinkage
    public void name(String name) {
        this.name = name;
    }

    @AllowChipmunkLinkage
    public void data(Map<String, String> data) {
        for (var entry : data.entrySet()) {
            var type = switch (entry.getValue()) {
                case "boolean" -> VarType.BOOLEAN;
                case "color" -> VarType.COLOR;
                case "distance" -> VarType.DISTANCE;
                case "float" -> VarType.FLOAT;
                case "image" -> VarType.IMAGE;
                case "string" -> VarType.STRING;
                case "svg" -> VarType.SVG;
                default -> null;
            };

            if (type != null) {
                componentInterface.defineParameter(entry.getKey(), type);
            } else {
                log.log(Level.WARNING, "Unsupported component data type {0}", entry.getValue());
            }
        }
    }

    @AllowChipmunkLinkage
    public StyleRule data(String varName) {
        return StyleRule.of(v -> v.get(varName));
    }

    @AllowChipmunkLinkage
    public StyleRule data(String varName, Object ruleOrValue) {
        return StyleRule.of(v -> {
            var value = v.get(varName);
            if (value == null) {
                value = evaluate(ruleOrValue, v);
            }
            return value;
        });
    }

    @AllowChipmunkLinkage
    public void state(String variable, Map<String, Object> style) {
        styles.add(StyleClass.forState(variable, StyleClass.normalize(style)));
    }

    @AllowChipmunkLinkage
    public void layer(String layer, Map<String, Object> style) {
        styles.add(StyleClass.forLayer(layer, StyleClass.normalize(style)));
    }

    @AllowChipmunkLinkage
    public void layer(String layer, String variable, Map<String, Object> style) {
        styles.add(StyleClass.forLayerState(layer, variable, StyleClass.normalize(style)));
    }

    @AllowChipmunkLinkage
    public void foreground(String layer, Map<String, Object> style) {
        styles.add(StyleClass.forForegroundLayer(layer, StyleClass.normalize(style)));
    }

    @AllowChipmunkLinkage
    public void foreground(String layer, String variable, Map<String, Object> style) {
        styles.add(StyleClass.forForegroundLayerState(layer, variable, StyleClass.normalize(style)));
    }

    public String getComponentName() {
        return name;
    }

    public ComponentInterface getComponentInterface() {
        return componentInterface;
    }

    public List<StyleClass> getStyles() {
        return styles;
    }

}
