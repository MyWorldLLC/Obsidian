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

package myworld.obsidian.geometry;

public record Bounds2D(Point2D origin, double width, double height) {

    public static final Bounds2D ZERO = new Bounds2D(new Point2D(0, 0), 0, 0);

    public double left(){
        return origin.x();
    }

    public double top(){
        return origin.y();
    }

    public double right(){
        return left() + width;
    }

    public double bottom(){
        return top() + height;
    }
}
