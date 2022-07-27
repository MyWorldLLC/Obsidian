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
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.jetbrains.skija.*;
import org.jetbrains.skija.impl.*;

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

    public void init(){

        glfwDefaultWindowHints();
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

        glfwSwapInterval(1); // Use VSync

        ui = ObsidianUI.createForGL(getWidth(), getHeight(), 0);
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
            ui.update(1.0/64.0); // Assume a constant refresh rate
            renderUI();
            ui.getDisplay().flush();
            glfwSwapBuffers(window);
        }
    }

    public void renderUI(){
        var canvas = ui.getDisplay().getCanvas();
        canvas.clear(0x000000FF);

        var paint = new Paint();
        paint.setColor(0xFFFFFFFF);
        paint.setStroke(false);

        canvas.drawLine(0, 0, 100, 100, paint);
    }

    public void cleanup(){
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }

    public int getWidth(){
        var width = new int[1];
        glfwGetFramebufferSize(window, width, null);
        return width[0];
    }

    public int getHeight(){
        var height = new int[1];
        glfwGetFramebufferSize(window, null, height);
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
}