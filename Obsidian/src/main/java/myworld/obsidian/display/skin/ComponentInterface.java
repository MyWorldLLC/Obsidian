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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ComponentInterface {

    protected final Map<String, VarType> parameters;

    public ComponentInterface(){
        parameters = new HashMap<>();
    }

    public void defineParameter(String name, VarType param){
        parameters.put(name, param);
    }

    public Map<String, VarType> getParameters(){
        return Collections.unmodifiableMap(parameters);
    }

    public boolean isDefined(String name, Variables params){
        return parameters.containsKey(name) && params.isDefined(name);
    }

    public boolean isActive(String name, Variables params){
        if(isDefined(name, params)){
            var value = params.get(name);
            if(value instanceof Boolean b){
                return b;
            }else if(value instanceof String s){
                return s.length() > 0;
            }else{
                return value != null;
            }
        }
        return false;
    }
}
