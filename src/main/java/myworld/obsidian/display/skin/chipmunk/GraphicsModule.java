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

package myworld.obsidian.display.skin.chipmunk;

import chipmunk.runtime.ChipmunkModule;
import chipmunk.vm.invoke.security.AllowChipmunkLinkage;
import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.display.Colors;
import myworld.obsidian.geometry.*;
import myworld.obsidian.text.TextDecoration;
import myworld.obsidian.text.TextShadow;

public class GraphicsModule implements ChipmunkModule {

    public static final String MODULE_NAME = "obsidian.graphics";

    protected static Distance parseDistance(String d){
        var dist = Distance.fromString(d);
        if(dist == null){
            throw new IllegalArgumentException(d + " is not a valid distance string");
        }
        return dist;
    }

    @AllowChipmunkLinkage
    public static final ColorRGBA white = Colors.WHITE;

    @AllowChipmunkLinkage
    public static final ColorRGBA black = Colors.BLACK;

    @AllowChipmunkLinkage
    public static String rgb(int r, int g, int b){
        return Colors.hex((byte)r, (byte)g, (byte)b);
    }

    @AllowChipmunkLinkage
    public static String rgba(int r, int g, int b, int a){
        return Colors.hex((byte)r, (byte)g, (byte)b, (byte)a);
    }

    @AllowChipmunkLinkage
    public ColorRGBA color(String hexColor){
        return ColorRGBA.of(hexColor);
    }

    @AllowChipmunkLinkage
    public SvgPath path(String path){
        return new SvgPath(path);
    }

    @AllowChipmunkLinkage
    public Ellipse circle(String radius){
        return Ellipse.circle(parseDistance(radius));
    }

    @AllowChipmunkLinkage
    public Ellipse oval(String width, String height){
        return new Ellipse(parseDistance(width), parseDistance(height));
    }

    @AllowChipmunkLinkage
    public Rectangle rectangle(String width, String height){
        return new Rectangle(parseDistance(width), parseDistance(height));
    }

    @AllowChipmunkLinkage
    public Rectangle square(String s){
        return Rectangle.square(parseDistance(s));
    }

    @AllowChipmunkLinkage
    public Move move(String x, String y){
        return new Move(parseDistance(x), parseDistance(y));
    }

    @AllowChipmunkLinkage
    public Move offset(String x, String y){
        return move(x, y);
    }

    @AllowChipmunkLinkage
    public Rotate rotate(Float degrees){
        return new Rotate(degrees);
    }

    @AllowChipmunkLinkage
    public Rotate rotate(Integer degrees){
        return new Rotate(degrees);
    }

    @AllowChipmunkLinkage
    public TextDecoration underline(){
        return underline((ColorRGBA) null, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public TextDecoration underline(String lineStyle){
        return underline(null, lineStyle, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public TextDecoration underline(Float thickness){
        return underline(null, TextDecoration.LineStyle.SOLID.name(), thickness);
    }

    @AllowChipmunkLinkage
    public TextDecoration underline(String lineStyle, Float thickness){
        return underline(null, lineStyle, thickness);
    }

    @AllowChipmunkLinkage
    public TextDecoration underline(ColorRGBA color){
        return underline(color, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public TextDecoration underline(ColorRGBA color, String lineStyle){
        return underline(color, lineStyle, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public TextDecoration underline(ColorRGBA color, Float thickness){
        return underline(color, TextDecoration.LineStyle.SOLID.name(), thickness);
    }

    @AllowChipmunkLinkage
    public TextDecoration underline(ColorRGBA color, String lineStyle, Float thickness){
        return TextDecoration.underline(color, TextDecoration.LineStyle.valueOf(lineStyle.trim().toUpperCase()), thickness);
    }

    @AllowChipmunkLinkage
    public TextDecoration strikeThrough(){
        return strikeThrough((ColorRGBA) null, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public TextDecoration strikeThrough(String lineStyle){
        return strikeThrough(null, lineStyle, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public TextDecoration strikeThrough(Float thickness){
        return strikeThrough(null, TextDecoration.LineStyle.SOLID.name(), thickness);
    }

    @AllowChipmunkLinkage
    public TextDecoration strikeThrough(String lineStyle, Float thickness){
        return strikeThrough(null, lineStyle, thickness);
    }

    @AllowChipmunkLinkage
    public TextDecoration strikeThrough(ColorRGBA color){
        return strikeThrough(color, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public TextDecoration strikeThrough(ColorRGBA color, String lineStyle){
        return strikeThrough(color, lineStyle, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public TextDecoration strikeThrough(ColorRGBA color, Float thickness){
        return TextDecoration.strikeThrough(color, thickness);
    }

    @AllowChipmunkLinkage
    public TextDecoration strikeThrough(ColorRGBA color, String lineStyle, Float thickness){
        return TextDecoration.strikeThrough(color, TextDecoration.LineStyle.valueOf(lineStyle.trim().toUpperCase()), thickness);
    }

    @AllowChipmunkLinkage
    public TextDecoration decoration(Boolean underline, Boolean strikeThrough, ColorRGBA color, String lineStyle, Float thickness){
        return new TextDecoration(underline, strikeThrough, color, TextDecoration.LineStyle.valueOf(lineStyle.trim().toUpperCase()), thickness);
    }

    @AllowChipmunkLinkage
    public TextShadow textShadow(Move offset, ColorRGBA color, Float blurSigma){
        return new TextShadow(offset, color, blurSigma);
    }

}
