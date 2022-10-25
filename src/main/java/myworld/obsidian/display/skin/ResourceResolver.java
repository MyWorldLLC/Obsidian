package myworld.obsidian.display.skin;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceResolver {

    InputStream resolve(String path) throws IOException;

}
