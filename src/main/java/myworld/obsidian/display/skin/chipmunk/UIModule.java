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
import myworld.obsidian.display.Colors;

public class UIModule implements ChipmunkModule {

    public static final String MODULE_NAME = "obsidian.ui";

    @AllowChipmunkLinkage
    public static String rgb(int r, int g, int b){
        return Colors.rgb(r, g, b);
    }

    @AllowChipmunkLinkage
    public static String rgba(int r, int g, int b, int a){
        return Colors.rgba(r, g, b, a);
    }

    @AllowChipmunkLinkage
    public static final String white = Colors.WHITE;

    @AllowChipmunkLinkage
    public static final String black = Colors.BLACK;

}
