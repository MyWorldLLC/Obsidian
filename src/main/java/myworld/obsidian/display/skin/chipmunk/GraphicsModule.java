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
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.geometry.Move;
import myworld.obsidian.geometry.Rotate;
import myworld.obsidian.geometry.SvgPath;

public class GraphicsModule implements ChipmunkModule {

    public static final String MODULE_NAME = "obsidian.graphics";

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
    public SvgPath path(String path){
        return new SvgPath(path);
    }

    @AllowChipmunkLinkage
    public ColorRGBA color(String hexColor){
        return ColorRGBA.of(hexColor);
    }

    @AllowChipmunkLinkage
    public Move move(String x, String y){
        var distX = Distance.fromString(x);
        if(distX == null){
            throw new IllegalArgumentException(x + " is not a valid distance string");
        }

        var distY = Distance.fromString(y);
        if(distY == null){
            throw new IllegalArgumentException(y + " is not a valid distance string");
        }

        return new Move(distX, distY);
    }

    @AllowChipmunkLinkage
    public Rotate rotate(Float degrees){
        return new Rotate(degrees);
    }

    @AllowChipmunkLinkage
    public Rotate rotate(Integer degrees){
        return new Rotate(degrees);
    }

}
