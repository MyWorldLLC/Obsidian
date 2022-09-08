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

package myworld.obsidian.geometry;

public record Distance(Number quantity, Unit unit) {

    public int asInt(){
        return quantity.intValue();
    }

    public float asFloat(){
        return quantity.floatValue();
    }

    @Override
    public String toString(){
        return quantity.toString() + unit.toString();
    }

    public static Distance fromString(String s){
        if(s.endsWith(Unit.PIXELS.symbol())){
            return new Distance(Integer.parseInt(s.substring(0, s.indexOf(Unit.PIXELS.symbol()))), Unit.PIXELS);
        }else if(s.endsWith(Unit.PERCENTAGE.symbol())){
            return new Distance(Integer.parseInt(s.substring(0, s.indexOf(Unit.PERCENTAGE.symbol()))), Unit.PERCENTAGE);
        }
        return null;
    }

}
