/*
 *    Copyright 2022 MyWorld, LLC

 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package myworld.obsidian.example;

import myworld.obsidian.ObsidianUI;
import myworld.obsidian.display.DisplayEngine;
import myworld.obsidian.display.skin.chipmunk.ChipmunkSkinLoader;
import myworld.obsidian.layout.LayoutDimension;
import myworld.obsidian.scene.Component;
import myworld.obsidian.text.Text;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.*;

public class ExampleRunner {

    protected long window;

    protected ObsidianUI ui;

    public static void exit(String msg){
        System.out.println(msg);
        System.exit(0);
    }

    public static void main(String [] args) throws Exception {
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()){
            exit("Unable to initialize GLFW");
        }

        var runner = new ExampleRunner();
        runner.init();
        runner.run();
        runner.cleanup();

        glfwTerminate();
    }

    public void init() throws Exception {
        glfwDefaultWindowHints();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        glfwWindowHint(GLFW_STENCIL_BITS, 0);
        glfwWindowHint(GLFW_DEPTH_BITS, 0);
        // This is crucial - without this, antialiasing will be disabled on the framebuffer
        // and any Skia operations involving clipping will result in major flickering and image
        // corruption across the entire drawing canvas.
        glfwWindowHint(GLFW_SAMPLES, 4);

        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);

        window = glfwCreateWindow(1200, 800, "Obsidian GL Demo", NULL, NULL);
        if(window == NULL){
            exit("Could not create GLFW window");
        }

        glfwMakeContextCurrent(window);

        glfwSetWindowSizeCallback(window, (win, width, height) -> {
            createSurface();
        });

        // TODO - user input

        GL.createCapabilities();
        glfwSwapInterval(1); // Use VSync

        ui = ObsidianUI.createForGL(getRenderWidth(), getRenderHeight(), 0);
        ui.registerSkin(ChipmunkSkinLoader.loadFromClasspath(ChipmunkSkinLoader.DEFAULT_SKIN));
        ui.useSkin("Obsidian");

        var example = new Component();
        example.styleName().set("Example");
        example.layout().preferredSize(LayoutDimension.pixels(100), LayoutDimension.pixels(100));
        ui.getRoot().addChild(example);

        var text = Text.create("Hello, World!", "ExampleText");
        example.data().set("text", text);

    }

    public int getRenderWidth(){
        return (int) (getWidth() / getXScale() * getDPI());
    }

    public int getRenderHeight(){
        return (int) (getHeight() / getYScale() * getDPI());
    }

    public void createSurface(){
        ui.display().ifSet(DisplayEngine::close);
        ui.setDisplay(DisplayEngine.createForGL(getRenderWidth(), getRenderHeight(), 0));
    }

    public void run(){
        glfwShowWindow(window);
        while(!glfwWindowShouldClose(window)){
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_STENCIL_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            ui.updateAndRender(1.0/64.0); // Assume a constant refresh rate
            glFinish();
            glfwSwapBuffers(window);
            sleep(16);
        }
    }

    public void cleanup(){
        ui.cleanup();
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    public int getWidth(){
        var width = new int[1];
        glfwGetWindowSize(window, width, null);
        return width[0];
    }

    public int getHeight(){
        var height = new int[1];
        glfwGetWindowSize(window, null, height);
        return height[0];
    }

    public float getXScale(){
        var scale = new float[1];
        glfwGetWindowContentScale(window, scale, null);
        return scale[0];
    }

    public float getYScale(){
        var scale = new float[1];
        glfwGetWindowContentScale(window, null, scale);
        return scale[0];
    }

    public float getDPI(){
        return getXScale();
    }

    protected void sleep(long millis){
        try {
            Thread.sleep(16);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }
}