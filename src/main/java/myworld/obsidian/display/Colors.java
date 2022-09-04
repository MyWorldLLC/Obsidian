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

    public static final String COLOR_RGB_HEX_PATTERN = "#RRGGBB";
    public static final String COLOR_RGBA_HEX_PATTERN = "#RRGGBBAA";

    public static String hex(byte r, byte g, byte b){
        return "#%1$02X%2$02X%3$02X".formatted(r, g, b);
    }

    public static String hex(byte r, byte g, byte b, byte a){
        return "#%1$02X%2$02X%3$02X%4$02X".formatted(r, g, b, a);
    }

    public static final ColorRGBA WHITE = ColorRGBA.of("#FFFFFF");

    public static final ColorRGBA BLACK = ColorRGBA.of("#000000");
}
