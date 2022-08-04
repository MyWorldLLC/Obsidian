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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComponentModule implements ChipmunkModule {

    public record LayerDef(String name, Map<String, Object> style){}

    protected String name;
    protected final Map<String, String> componentInterface = new HashMap<>();

    protected final List<LayerDef> layers = new ArrayList<>();
    protected final Map<String, Map<String, Object>> stateStyles = new HashMap<>();

    @AllowChipmunkLinkage
    public void name(String name){
        this.name = name;
    }

    @AllowChipmunkLinkage
    public void data(Map<String, String> data){
        componentInterface.putAll(data);
    }

    @AllowChipmunkLinkage
    public void state(String variable, Map<String, Object> style){
        stateStyles.put(variable, style);
    }

    @AllowChipmunkLinkage
    public void layer(String layer, Map<String, Object> style){
        layers.add(new LayerDef(layer, style));
    }

    public String getComponentName(){
        return name;
    }

    public Map<String, String> getComponentInterface(){
        return componentInterface;
    }

    public Map<String, Map<String, Object>> getStateStyles(){
        return stateStyles;
    }

    public List<LayerDef> getLayers(){
        return layers;
    }

}
