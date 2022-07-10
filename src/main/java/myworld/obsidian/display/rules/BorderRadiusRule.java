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

package myworld.obsidian.display.rules;

public record BorderRadiusRule(float ul, float ur, float lr, float ll) {

    public BorderRadiusRule {
        if(ul < 0 || ur < 0 || lr < 0 || ll < 0){
            throw new IllegalArgumentException("Border radii must not be negative");
        }
    }

    public BorderRadiusRule(float radius){
        this(radius, radius, radius, radius);
    }
}
