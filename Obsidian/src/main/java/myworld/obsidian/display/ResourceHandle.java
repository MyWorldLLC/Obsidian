package myworld.obsidian.display;

public record ResourceHandle(Type type, String path) {
    public enum Type {
        IMAGE, SVG
    }

    public static ResourceHandle image(String path){
        return new ResourceHandle(Type.IMAGE, path);
    }

    public static ResourceHandle svg(String path){
        return new ResourceHandle(Type.SVG, path);
    }
}
