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

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ObsidianPixels {

    public enum Format {
        RGBA8, ARGB8, BGRA8_PRE
    }

    protected final ByteBuffer buff;
    protected final IntBuffer intBuff;

    protected final Format format;
    protected final int vPixels;
    protected final int wPixels;

    public ObsidianPixels(int wPixels, int vPixels){
        this(Format.RGBA8, wPixels, vPixels);
    }

    public ObsidianPixels(Format format, int wPixels, int vPixels){
        this.format = format;
        this.wPixels = wPixels;
        this.vPixels = vPixels;

        buff = ByteBuffer.allocate(wPixels * vPixels * 4);
        intBuff = buff.asIntBuffer();
    }

    public ByteBuffer rawBuffer() {
        return buff;
    }
    public IntBuffer pixelBuffer() {
        return intBuff;
    }
    public int getVPixels() {
        return vPixels;
    }
    public int getWPixels() {
        return wPixels;
    }
    public Format getFormat(){
        return format;
    }
}
