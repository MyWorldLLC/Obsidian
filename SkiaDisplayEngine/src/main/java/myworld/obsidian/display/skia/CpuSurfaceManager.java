package myworld.obsidian.display.skia;

import io.github.humbleui.skija.Surface;

public class CpuSurfaceManager implements SurfaceManager {

    protected Surface surface;

    public CpuSurfaceManager(int width, int height){
        makeSurface(width, height);
    }

    @Override
    public void resize(int width, int height) {
        makeSurface(width, height);
    }

    protected void makeSurface(int width, int height){
        if(surface != null){
            surface.close();
        }

        surface = Surface.makeRaster(SkiaDisplayEngine.getImageInfo(width, height));
    }

    @Override
    public Surface getSurface() {
        return surface;
    }

    @Override
    public void close() {
        surface.close();
    }
}
