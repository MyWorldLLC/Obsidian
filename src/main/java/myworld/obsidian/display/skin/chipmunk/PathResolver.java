package myworld.obsidian.display.skin.chipmunk;

import java.io.IOException;
import java.io.InputStream;

public interface PathResolver {

    InputStream resolve(String path) throws IOException;

}
