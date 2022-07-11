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

package myworld.obsidian.display.skins;

import myworld.obsidian.display.RenderLayer;
import myworld.obsidian.properties.ListProperty;
import myworld.obsidian.scene.Component;

public abstract class Skin<T extends Component> {

    protected final T component;
    protected final ListProperty<RenderLayer> renderLayers;

    public Skin(T component){
        this.component = component;
        renderLayers = new ListProperty<>();
    }

    public T component(){
        return component;
    }

    public ListProperty<RenderLayer> renderLayers(){
        return renderLayers;
    }

    public void onAttach(){}

    public void onDetach(){}

}
