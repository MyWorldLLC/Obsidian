package myworld.obsidian.display.skia;

import io.github.humbleui.skija.Surface;

public interface SurfaceManager extends AutoCloseable {

    void resize(int width, int height);
    Surface getSurface();
    void close();

}
