package myworld.obsidian.display.skin.resolvers;

import java.util.ArrayList;
import java.util.List;

public class ResolverUtil {

    public static String resolvePath(String skinPath, String resourcePath){
        var pathParts = new ArrayList<>(List.of(skinPath.split("/")));
        pathParts.add(resourcePath);
        return String.join("/", pathParts.toArray(new String[]{})).replaceAll("//", "/");
    }
}
