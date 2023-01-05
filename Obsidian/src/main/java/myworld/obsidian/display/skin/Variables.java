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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Variables {

    public static Variables empty(){
        return new Variables();
    }

    protected final Map<String, Object> vars;

    public Variables(){
        vars = new ConcurrentHashMap<>();
    }

    public Variables(Variables from){
        vars = new ConcurrentHashMap<>(from.vars);
    }

    public Variables(Map<String, Object> from){
        vars = new ConcurrentHashMap<>(from);
    }

    public Object get(String name){
        return vars.get(name);
    }

    public <T> T get(String name, Class<T> type){
        var value = vars.get(name);
        if(value == null || !type.isAssignableFrom(value.getClass())){
            return null;
        }

        return type.cast(value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String name, T defaultValue){
        var value = get(name, defaultValue.getClass());
        return value != null ? (T) value : defaultValue;
    }

    public void set(String name, Object value){
        vars.put(name, value);
    }

    public void set(Map<String, Object> vars){
        this.vars.putAll(vars);
    }

    public boolean isDefined(String name){
        return vars.containsKey(name);
    }

    public <T> boolean isDefined(String name, Class<T> type){
        return get(name, type) != null;
    }

    @Override
    public String toString(){
        return vars.toString();
    }
}
