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

package myworld.obsidian.text;

import myworld.obsidian.ObsidianPixels;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTFontinfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static org.lwjgl.stb.STBTruetype.stbtt_InitFont;

public class TextEngine {

    protected final List<FontLoader> loaders;
    protected final Map<String, FontData> fonts;

    public TextEngine(){
        loaders = Collections.synchronizedList(new ArrayList<>());
        fonts = new HashMap<>();
    }

    public void registerLoader(FontLoader loader){
        loaders.add(loader);
    }

    public ObsidianPixels render(String text, FontFace font){

        var data = fonts.get(font.name());
        if(data == null){
            data = initFace(font);
        }

        // TODO - layout & render text

        return null;
    }

    public float visualOffset(String text, FontFace font, int caretIndex, float caretWidth){
        return 0;
    }

    protected FontData initFace(FontFace font){
        if(font.name().equals(FontFace.DEFAULT_FONT)){
            return null;
        }

        try (var fontStream = locateFont(font.name())){
            var bytes = fontStream.readAllBytes();
            var buffer = BufferUtils.createByteBuffer(bytes.length);
            buffer.put(bytes);
            buffer.flip();

            var fontData = new FontData();
            if(!stbtt_InitFont(fontData.getFontInfo(), buffer)){
                throw new RuntimeException("Invalid font data: " + font.name());
            }

            fonts.put(font.name(), fontData);

            // TODO - glyph bitmaps

            return fontData;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    protected InputStream locateFont(String font) throws IOException  {
        for(var loader : loaders){
            var stream = loader.openFont(font);
            if(stream != null){
                return stream;
            }
        }
        return null;
    }

    protected static class FontData{

        protected final STBTTFontinfo fontInfo;

        public FontData(){
            fontInfo = STBTTFontinfo.create();
        }

        public STBTTFontinfo getFontInfo(){
            return fontInfo;
        }

        protected void close(){
            fontInfo.close();
        }
    }

}
