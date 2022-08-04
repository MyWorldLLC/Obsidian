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

package myworld.obsidian.display;

public class Colors {

    public static String rgb(int r, int g, int b){
        return "#%1$02X%2$02X%3$02X".formatted(r, g, b);
    }

    public static String rgba(int r, int g, int b, int a){
        return "#%1$02X%2$02X%3$02X%4$02X".formatted(r, g, b, a);
    }

    public static final String WHITE = "#FFFFFF";

    public static final String BLACK = "#000000";
}