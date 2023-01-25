package myworld.obsidian.display;

import io.github.humbleui.skija.*;
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

    public static ObsidianImage fromPixels(ObsidianPixels pixels){
        var format = pixels.getFormat();
        var colorSpace = pixels.getColorSpace();
        var colorInfo = new ColorInfo(skiaColorType(format), skiaAlphaType(format), skiaColorSpace(colorSpace));
        var info = new ImageInfo(colorInfo, pixels.getWPixels(), pixels.getVPixels());
        var image = Image.makeRaster(info, pixels.rawBuffer().array(), 4L * pixels.getWPixels());
        return new ObsidianImage(image);
    }

    protected static ColorType skiaColorType(ObsidianPixels.Format format){
        return switch (format){
            case RGBA8 -> ColorType.RGBA_8888;
            case BGRA8_PRE -> ColorType.BGRA_8888;
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    protected static ColorAlphaType skiaAlphaType(ObsidianPixels.Format format){
        return switch (format){
            case RGBA8 -> ColorAlphaType.UNPREMUL;
            case BGRA8_PRE -> ColorAlphaType.PREMUL;
            default -> throw new IllegalArgumentException("Unsupported format: " + format);
        };
    }

    protected static ColorSpace skiaColorSpace(ObsidianPixels.ColorSpace colorSpace){
        return switch (colorSpace){
            case SRGB -> ColorSpace.getSRGB();
            case LINEAR -> ColorSpace.getSRGBLinear();
        };
    }
}
