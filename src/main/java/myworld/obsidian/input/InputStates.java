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

package myworld.obsidian.input;

import java.util.HashMap;
import java.util.Map;

public class InputStates {

    protected final Map<Integer, Boolean> states;

    public InputStates(){
        states = new HashMap<>();
    }

    public boolean get(int key, boolean defaultState){
        return states.getOrDefault(key, defaultState);
    }

    public boolean get(int key){
        return get(key, false);
    }

    public boolean set(int key, boolean state){
        var former = states.put(key, state);
        return former != null && former;
    }
}
