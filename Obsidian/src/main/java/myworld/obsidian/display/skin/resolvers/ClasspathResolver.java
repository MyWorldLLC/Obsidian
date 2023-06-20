package myworld.obsidian.display.skin.resolvers;

import myworld.obsidian.display.skin.ResourceResolver;

import java.io.InputStream;

public record ClasspathResolver(Class<?> resolveFrom, String skinPath) implements ResourceResolver {
    public ClasspathResolver(String skinPath){
        this(ClasspathResolver.class, skinPath);
    }
    @Override
    public InputStream resolve(String path) {
        return resolveFrom.getResourceAsStream(ResolverUtil.resolvePath(skinPath, path));
    }
}
