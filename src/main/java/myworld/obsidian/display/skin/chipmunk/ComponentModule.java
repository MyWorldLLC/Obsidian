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
import myworld.obsidian.geometry.Dimension2D;
import myworld.obsidian.geometry.Rectangle;
import myworld.obsidian.geometry.SvgPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ComponentModule implements ChipmunkModule {

    public static final String MODULE_NAME = "obsidian.component";

    protected final List<StyleClass> styles = new ArrayList<>();

    protected String name;
    protected final ComponentInterface componentInterface = new ComponentInterface();

    @AllowChipmunkLinkage
    public void name(String name){
        this.name = name;
    }

    @AllowChipmunkLinkage
    public void data(Map<String, String> data){
        for(var entry : data.entrySet()){
            var type = switch(entry.getValue()){
                case "string" -> VarType.STRING;
                case "boolean" -> VarType.BOOLEAN;
                case "image" -> VarType.IMAGE;
                case "color" -> VarType.COLOR;
                default -> null;
            };

            if(type != null){
                componentInterface.defineParameter(entry.getKey(), type);
            }else{
                // TODO - log warning
            }
        }
    }

    @AllowChipmunkLinkage
    public void state(String variable, Map<String, Object> style){
        styles.add(StyleClass.forState(variable, StyleClass.toRules(style)));
    }

    @AllowChipmunkLinkage
    public void layer(String layer, Map<String, Object> style){
        styles.add(StyleClass.forLayer(layer, StyleClass.toRules(style)));
    }

    @AllowChipmunkLinkage
    public void layer(String layer, String variable, Map<String, Object> style){
        styles.add(StyleClass.forLayerState(layer, variable, StyleClass.toRules(style)));
    }

    @AllowChipmunkLinkage
    public SvgPath path(String path){
        return new SvgPath(path);
    }

    public String getComponentName(){
        return name;
    }

    public ComponentInterface getComponentInterface(){
        return componentInterface;
    }

    public List<StyleClass> getStyles(){
        return styles;
    }

}
