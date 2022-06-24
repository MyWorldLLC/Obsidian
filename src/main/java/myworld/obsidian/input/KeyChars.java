package myworld.client.obsidian.input;

import java.util.HashMap;
import java.util.Map;

public class KeyChars {

    protected final Map<Integer, char[]> cachedChars;

    public KeyChars(){
        cachedChars = new HashMap<>();
    }

    public char[] charsFor(int keyCode){
        return cachedChars.computeIfAbsent(keyCode, c -> new char[]{(char) c.intValue()});
    }
}
