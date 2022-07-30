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

public record ColorRGBA(byte r, byte g, byte b, byte a) {

    public ColorRGBA(int color){
        this(extractChannel(color, 24), extractChannel(color, 16), extractChannel(color, 8), extractChannel(color, 0));
    }
    public int toInt(){
        return (r << 24) | (g << 16) | (b << 8) | a;
    }

    public static byte extractChannel(int color, int shift){
        return (byte) (color >> shift);
    }
}
