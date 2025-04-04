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
import myworld.obsidian.properties.ListProperty;

import java.util.function.Predicate;

public class Typeset implements AutoCloseable {

    protected final ListProperty<Typeface> faces;

    public Typeset(){
        faces = new ListProperty<>();
    }

    public ListProperty<Typeface> loadedFaces(){
        return faces;
    }

    public void add(Typeface face){
        if(!contains(face)){
            faces.add(face);
        }
    }

    public Typeface get(TypeFeatures features){
        return faces.stream()
                .filter(f -> hasFeatures(f, features))
                .findFirst()
                .orElse(null);
    }

    public void remove(Typeface face){
        faces.removeIf(f -> hasSameFeatures(f, face));
    }

    public boolean contains(TypeFeatures features){
        return faces.stream().anyMatch(f -> hasFeatures(f, features));
    }

    public boolean contains(Typeface face){
        return faces.stream().anyMatch(f -> hasSameFeatures(f, face));
    }

    public void clear(){
        faces.forEach(Typeface::close);
        faces.clear();
    }

    @Override
    public void close(){
        clear();
    }

    public Typeface first(Predicate<Typeface> p) {
        return faces.stream().filter(p).findFirst().orElse(null);
    }

    public static boolean hasSameFeatures(Typeface a, Typeface b){
        return hasFeatures(a, TypeFeatures.forTypeface(b));
    }

    public static boolean hasFeatures(Typeface a, TypeFeatures f){
        return a.getFamilyName().equals(f.familyName())
                && a.isBold() == f.isBold()
                && a.isItalic() == f.isItalic()
                && a.isFixedPitch() == f.isMonospace();
    }
}
