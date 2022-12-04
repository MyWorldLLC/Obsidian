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

    public int toRGBA(){
        return channel(r, 3) | channel(g, 2) | channel(b, 1) | channel(a, 0);
    }

    public int toARGB(){
        return channel(a, 3) | channel(r, 2) | channel(g, 1) | channel(b, 0);
    }

    private int channel(byte b, int channel){
        return ((((int) b) & 0xFF) << 8 * channel);
    }

    public String toHex(){
        return Colors.hex(r, g, b, a);
    }

    @Override
    public String toString(){
        return "ColorRGBA[" + toHex() + "]";
    }

    public static byte extractChannel(int color, int shift){
        return (byte) (color >> shift);
    }

    public static ColorRGBA of(int r, int g, int b){
        return of(r, g, b, 255);
    }

    public static ColorRGBA of(int r, int g, int b, int a){
        return new ColorRGBA((byte)r, (byte)g, (byte) b, (byte)a);
    }

    public static ColorRGBA of(String hex){
        if(hex.length() != Colors.COLOR_RGB_HEX_PATTERN.length() && hex.length() != Colors.COLOR_RGBA_HEX_PATTERN.length()){
            throw new IllegalArgumentException("Invalid hex color string: must match either %s or %s"
                    .formatted(Colors.COLOR_RGB_HEX_PATTERN, Colors.COLOR_RGBA_HEX_PATTERN));
        }

        String r = hex.substring(1, 3);
        String g = hex.substring(3, 5);
        String b = hex.substring(5, 7);
        String a = "FF";
        if(hex.length() == Colors.COLOR_RGBA_HEX_PATTERN.length()){
            a = hex.substring(7, 9);
        }

        final int radix = 16;
        return ColorRGBA.of(Integer.parseInt(r, radix), Integer.parseInt(g, radix), Integer.parseInt(b, radix), Integer.parseInt(a, radix));
    }
}
