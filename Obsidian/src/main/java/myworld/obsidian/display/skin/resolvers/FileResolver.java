package myworld.obsidian.display.skin.resolvers;

import myworld.obsidian.display.skin.ResourceResolver;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public record FileResolver(String basePath) implements ResourceResolver {

    @Override
    public InputStream resolve(String path) throws IOException {
        return Files.newInputStream(Path.of(basePath).resolve(path));
    }

}
