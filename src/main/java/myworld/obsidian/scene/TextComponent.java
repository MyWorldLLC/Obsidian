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

import myworld.obsidian.properties.ValueChangeListener;
import myworld.obsidian.properties.ValueProperty;

public class TextComponent extends Component {

    protected final ValueProperty<String> text;
    protected final ValueChangeListener<String> listener;

    public TextComponent(ValueProperty<String> text){
        this.text = text;
        listener = this::onTextChange;
    }

    protected void onTextChange(ValueProperty<String> prop, String oldValue, String newValue){
        requestVisualUpdate();
    }

    @Override
    public void onAttach(){
        text.addListener(listener);
    }

    @Override
    public void onDetach(){
        text.removeListener(listener);
    }
}
