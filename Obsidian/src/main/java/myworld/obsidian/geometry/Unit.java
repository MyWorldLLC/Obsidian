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

public enum Unit {
    PIXELS("px"), PERCENTAGE("%");

    private final String symbol;

    Unit(String symbol){
        this.symbol = symbol;
    }

    public String symbol(){
        return symbol;
    }

    @Override
    public String toString(){
        return symbol;
    }

    public static Unit fromString(String s){
        return switch (s){
            case "px" -> PIXELS;
            case "%" -> PERCENTAGE;
            default -> null;
        };
    }
}
