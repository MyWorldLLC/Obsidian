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
import java.util.function.Function;

public record ComponentSkin(String name, ComponentInterface parameters, StyleClass... styleClasses) {

    public List<String> layerNames(){
        return Arrays.stream(styleClasses)
                .filter(StyleClass::isLayer)
                .map(StyleClass::layer)
                .distinct()
                .toList();
    }

    public List<StyleClass> layers(){
        return Arrays.stream(styleClasses)
                .filter(StyleClass::isLayer)
                .toList();
    }

    public List<StyleClass> activeForLayer(String layer, Variables params){
        return activeForLayer(layer, params, s -> s);
    }

    public List<StyleClass> activeForLayer(String layer, Variables params, Function<StyleClass, StyleClass> mapper){
        return Arrays.stream(styleClasses)
                .filter(StyleClass::isLayer)
                .filter(s -> s.layer().equals(layer))
                .filter(s -> isActive(s, params))
                .map(mapper)
                .toList();
    }

    public boolean isActive(StyleClass style, Variables params){
        return !style.isStateLimited() || parameters.isActive(style.stateParam(), params);
    }

    public StyleClass componentClass(){
        return findNamed(name);
    }

    public StyleClass findNamed(String name){
        return Arrays.stream(styleClasses).filter(s -> s.name() != null && s.name().equals(name)).findFirst().orElse(null);
    }
}
