package example;

import myworld.obsidian.input.ClipboardHandler;
import org.lwjgl.glfw.GLFW;

public class GLFWClipboard implements ClipboardHandler {

    protected final long window;

    public GLFWClipboard(long window){
        this.window = window;
    }

    @Override
    public void toClipboard(String s) {
        GLFW.glfwSetClipboardString(window, s);
    }

    @Override
    public String fromClipboard() {
        return GLFW.glfwGetClipboardString(window);
    }
}
