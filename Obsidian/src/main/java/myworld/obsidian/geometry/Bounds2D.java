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

public record Bounds2D(Point2D origin, float width, float height) {

    public static final Bounds2D UNDEFINED = new Bounds2D(new Point2D(-1, -1), -1, -1);

    public float left(){
        return origin.x();
    }

    public float top(){
        return origin.y();
    }

    public float right(){
        return left() + width;
    }

    public float bottom(){
        return top() + height;
    }

    public Bounds2D translate(float dx, float dy){
        return new Bounds2D(new Point2D(origin.x() + dx, origin.y() + dy), width, height);
    }

    public Bounds2D resize(float width, float height){
        return new Bounds2D(origin, width, height);
    }
}
