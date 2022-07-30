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

package myworld.obsidian.display.skin;

import java.util.Arrays;
import java.util.List;

public record ComponentSkin(String name, ComponentInterface parameters, StyleClass... styleClasses) {

    public List<StyleClass> layers(){
        return Arrays.stream(styleClasses).filter(StyleClass::isLayer).toList();
    }

    public List<StyleClass> activeForLayer(String layer, Variables params){
        return Arrays.stream(styleClasses)
                .filter(StyleClass::isLayer)
                .filter(s -> s.layer().equals(layer))
                .filter(s -> s.isStateLimited() && parameters.isDefined(s.stateParam(), params))
                .toList();
    }

    public StyleClass componentClass(){
        return findNamed(name);
    }

    public StyleClass findNamed(String name){
        return Arrays.stream(styleClasses).filter(s -> s.name().equals(name)).findFirst().orElse(null);
    }
}
