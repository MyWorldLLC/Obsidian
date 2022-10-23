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

public record Distance(Number value, Unit unit) {

    public static Distance of(float value, Unit unit){
        return new Distance(value, unit);
    }

    public static Distance pixels(float value){
        return of(value, Unit.PIXELS);
    }

    public static Distance percentage(float value){
        return of(value, Unit.PERCENTAGE);
    }

    public int asInt(){
        return value.intValue();
    }

    public float asFloat(){
        return value.floatValue();
    }

    public Distance multiply(float value){
        return new Distance(this.value.floatValue() * value, unit);
    }

    public Distance add(float value){
        return new Distance(this.value.floatValue() + value, unit);
    }

    public Distance subtract(float value){
        return new Distance(this.value.floatValue() - value, unit);
    }

    public Distance divide(float value){
        return new Distance(this.value.floatValue() / value, unit);
    }

    @Override
    public String toString(){
        return value.toString() + unit.toString();
    }

    public static Distance fromString(String s){
        if(s.endsWith(Unit.PIXELS.symbol())){
            return new Distance(Float.parseFloat(s.substring(0, s.indexOf(Unit.PIXELS.symbol()))), Unit.PIXELS);
        }else if(s.endsWith(Unit.PERCENTAGE.symbol())){
            return new Distance(Float.parseFloat(s.substring(0, s.indexOf(Unit.PERCENTAGE.symbol()))), Unit.PERCENTAGE);
        }
        return null;
    }

}
