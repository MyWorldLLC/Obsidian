package myworld.obsidian.display;

import io.github.humbleui.skija.Image;
import myworld.obsidian.ObsidianPixels;

public class ObsidianImage {

    protected final Image image;

    public ObsidianImage(Image image){
        this.image = image;
    }

    protected Image getImage(){
        return image;
    }

    public int height(){
        return image.getHeight();
    }

    public int width(){
        return image.getWidth();
    }

    public ObsidianPixels toPixels(){
        return null; // TODO - read Skia pixels to ObsidianPixels
    }

    public static ObsidianImage fromPixels(ObsidianPixels pixels){
        return null; // TODO - load Skia image from pixels
    }
}
