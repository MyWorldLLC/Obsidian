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
import myworld.obsidian.display.*;
import myworld.obsidian.display.skin.StyleRule;
import myworld.obsidian.geometry.*;
import myworld.obsidian.text.TextDecoration;
import myworld.obsidian.text.TextShadow;

import static myworld.obsidian.display.skin.StyleClass.evaluate;

public class GraphicsModule implements ChipmunkModule {

    public static final String MODULE_NAME = "obsidian.graphics";

    protected static Distance parseDistance(String d){
        var dist = Distance.fromString(d);
        if(dist == null){
            throw new IllegalArgumentException(d + " is not a valid distance string");
        }
        return dist;
    }

    protected static Distance coerceDistance(Object d){
        if(d instanceof Distance dist){
            return dist;
        }else{
            return parseDistance((String) d);
        }
    }

    @AllowChipmunkLinkage
    public static final ColorRGBA white = Colors.WHITE;

    @AllowChipmunkLinkage
    public static final ColorRGBA black = Colors.BLACK;

    @AllowChipmunkLinkage
    public static final ColorRGBA red = Colors.RED;

    @AllowChipmunkLinkage
    public static final ColorRGBA green = Colors.GREEN;

    @AllowChipmunkLinkage
    public static final ColorRGBA blue = Colors.BLUE;

    @AllowChipmunkLinkage
    public static final ColorRGBA transparent = Colors.TRANSPARENT;

    @AllowChipmunkLinkage
    public static String rgb(int r, int g, int b){
        return Colors.hex((byte)r, (byte)g, (byte)b);
    }

    @AllowChipmunkLinkage
    public static String rgba(int r, int g, int b, int a){
        return Colors.hex((byte)r, (byte)g, (byte)b, (byte)a);
    }

    @AllowChipmunkLinkage
    public StyleRule pixels(String varName){
        return StyleRule.of(v -> Distance.pixels(v.get(varName, Number.class)));
    }

    @AllowChipmunkLinkage
    public StyleRule percentage(String varName){
        return StyleRule.of(v -> Distance.percentage(v.get(varName, Number.class)));
    }

    @AllowChipmunkLinkage
    public StyleRule color(Object hexColor){
        return StyleRule.of(v -> ColorRGBA.of(evaluate(hexColor, v)));
    }

    @AllowChipmunkLinkage
    public StyleRule svg(String varName){
        return StyleRule.of(v -> v.get(varName, ObsidianSvg.class));
    }

    @AllowChipmunkLinkage
    public StyleRule svgResource(String path){
        return StyleRule.constant(ResourceHandle.svg(path));
    }

    @AllowChipmunkLinkage
    public StyleRule image(String varName){
        return StyleRule.of(v -> v.get(varName, ObsidianImage.class));
    }

    @AllowChipmunkLinkage
    public StyleRule imageResource(String path){
        return StyleRule.constant(ResourceHandle.image(path));
    }

    @AllowChipmunkLinkage
    public StyleRule path(Object path){
        return StyleRule.of(v -> new SvgPath(evaluate(path, v)));
    }

    @AllowChipmunkLinkage
    public StyleRule circle(Object radius){
        return StyleRule.of(v -> Ellipse.circle(coerceDistance(evaluate(radius, v))));
    }

    @AllowChipmunkLinkage
    public StyleRule oval(Object width, Object height){
        return StyleRule.of(v -> new Ellipse(coerceDistance(evaluate(width, v)), coerceDistance(evaluate(height, v))));
    }

    @AllowChipmunkLinkage
    public StyleRule rectangle(Object width, Object height){
        return StyleRule.of(v ->new Rectangle(coerceDistance(evaluate(width, v)), coerceDistance(evaluate(height, v))));
    }

    @AllowChipmunkLinkage
    public StyleRule roundedRectangle(Object width, Object height, Object radius){
        return StyleRule.of(v ->RoundedRectangle.uniform(
                new Rectangle(
                        coerceDistance(evaluate(width, v)),
                        coerceDistance(evaluate(height, v))
                ),
                coerceDistance(evaluate(radius, v))));
    }
    @AllowChipmunkLinkage
    public StyleRule roundedRectangle(Object width, Object height, Object sideRadius, Object topRadius){
        return StyleRule.of(v ->new RoundedRectangle(
                new Rectangle(
                        coerceDistance(evaluate(width, v)),
                        coerceDistance(evaluate(height, v))
                ),
                coerceDistance(evaluate(sideRadius, v)),
                coerceDistance(evaluate(topRadius, v)),
                coerceDistance(evaluate(sideRadius, v)),
                coerceDistance(evaluate(topRadius, v))));
    }
    @AllowChipmunkLinkage
    public StyleRule roundedRectangle(Object width, Object height, Object leftRadius, Object topRadius, Object rightRadius, Object bottomRadius){
        return StyleRule.of(v ->new RoundedRectangle(
                new Rectangle(
                        coerceDistance(evaluate(width, v)),
                        coerceDistance(evaluate(height, v))
                ),
                coerceDistance(evaluate(leftRadius, v)),
                coerceDistance(evaluate(topRadius, v)),
                coerceDistance(evaluate(rightRadius, v)),
                coerceDistance(evaluate(bottomRadius, v))));
    }

    @AllowChipmunkLinkage
    public StyleRule square(Object s){
        return StyleRule.of(v -> Rectangle.square(coerceDistance(evaluate(s, v))));
    }

    @AllowChipmunkLinkage
    public StyleRule move(Object x, Object y){
        return StyleRule.of(v -> new Move(coerceDistance(evaluate(x, v)), coerceDistance(evaluate(y, v))));
    }

    @AllowChipmunkLinkage
    public StyleRule offset(Object x, Object y){
        return move(x, y);
    }

    @AllowChipmunkLinkage
    public StyleRule rotate(Object degrees){
        return StyleRule.of(v -> new Rotate(evaluate(degrees, v)));
    }

    @AllowChipmunkLinkage
    public StyleRule underline(){
        return underline(null, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public StyleRule underline(Object color){
        return underline(color, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public StyleRule underline(Object color, Object thickness){
        return underline(color, TextDecoration.LineStyle.SOLID.name(), thickness);
    }

    protected StyleRule underline(Object color, Object lineStyle, Object thickness){
        return StyleRule.of(v -> TextDecoration.underline(
                evaluate(color, v),
                TextDecoration.LineStyle.valueOf(((String)evaluate(lineStyle, v)).trim().toUpperCase()),
                evaluate(thickness, v)));
    }

    @AllowChipmunkLinkage
    public StyleRule strikeThrough(){
        return strikeThrough(null, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public StyleRule strikeThrough(Object color){
        return strikeThrough(color, TextDecoration.DEFAULT_THICKNESS);
    }

    @AllowChipmunkLinkage
    public StyleRule strikeThrough(Object color, Object thickness){
        return strikeThrough(color, TextDecoration.LineStyle.SOLID.name(), thickness);
    }

    protected StyleRule strikeThrough(Object color, Object lineStyle, Object thickness){
        return StyleRule.of(v ->TextDecoration.strikeThrough(
                evaluate(color, v),
                TextDecoration.LineStyle.valueOf(((String)evaluate(lineStyle, v)).trim().toUpperCase()),
                evaluate(thickness, v)));
    }

    @AllowChipmunkLinkage
    public StyleRule decoration(Object underline, Object strikeThrough, Object color, Object lineStyle, Object thickness){
        return StyleRule.of(v -> new TextDecoration(
                evaluate(underline, v),
                evaluate(strikeThrough, v),
                evaluate(color, v),
                TextDecoration.LineStyle.valueOf(((String)evaluate(lineStyle, v)).trim().toUpperCase()),
                evaluate(thickness, v)));
    }

    @AllowChipmunkLinkage
    public StyleRule textShadow(Object offset, Object color, Object blurSigma){
        return StyleRule.of(v -> new TextShadow(evaluate(offset, v), evaluate(color, v), evaluate(blurSigma, v)));
    }

}
