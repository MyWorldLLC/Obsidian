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

package myworld.obsidian.display.skia;

import io.github.humbleui.skija.Typeface;

public record TypeFeatures(String familyName, boolean isBold, boolean isItalic, boolean isMonospace) {

    public static TypeFeatures forTypeface(Typeface face){
        return new TypeFeatures(face.getFamilyName(), face.isBold(), face.isItalic(), face.isFixedPitch());
    }

}
