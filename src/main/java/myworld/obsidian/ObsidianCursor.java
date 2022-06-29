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

package myworld.obsidian;

public class ObsidianCursor {

    public enum Type {
        SIMPLE, IMAGE
    }

    protected final Type type;
    protected final String cursorType;
    protected final ObsidianPixels cursorPixels;
    protected final double hotspotX;
    protected final double hotspotY;

    private ObsidianCursor(Type type, String cursorType, ObsidianPixels cursorPixels, double hotspotX, double hotspotY){
        this.type = type;
        this.cursorType = cursorType;
        this.cursorPixels = cursorPixels;
        this.hotspotX = hotspotX;
        this.hotspotY = hotspotY;
    }

    public static ObsidianCursor ofType(String cursorType){
        return new ObsidianCursor(Type.SIMPLE, cursorType, null, 0, 0);
    }

    public static ObsidianCursor ofImage(ObsidianPixels pixels, double hotspotX, double hotspotY){
        return new ObsidianCursor(Type.IMAGE, null, pixels, hotspotX, hotspotY);
    }

    public Type getType() {
        return type;
    }

    public String getCursorType() {
        return cursorType;
    }

    public ObsidianPixels getCursorPixels() {
        return cursorPixels;
    }

    public double getHotspotX() {
        return hotspotX;
    }

    public double getHotspotY() {
        return hotspotY;
    }
}
