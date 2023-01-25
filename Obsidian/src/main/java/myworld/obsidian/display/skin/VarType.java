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

import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.display.ObsidianImage;
import myworld.obsidian.display.Svg;

public enum VarType {
    BOOLEAN(Boolean.class),
    COLOR(ColorRGBA.class),
    FLOAT(Float.class),
    IMAGE(ObsidianImage.class),
    SVG(Svg.class),
    INTEGER(Integer.class),
    STRING(String.class);

    private final Class<?> typeCls;

    VarType(Class<?> typeCls){
        this.typeCls = typeCls;
    }

    public <T> boolean matches(T param){
        return param == null || matches(param.getClass());
    }

    public <T> boolean matches(Class<T> paramCls){
        return typeCls.isAssignableFrom(paramCls);
    }
}
