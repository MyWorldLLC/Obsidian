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
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.*;

import org.jetbrains.skija.*;
import org.jetbrains.skija.impl.*;

public class ExampleRunner {

    protected long window;

    protected DirectContext context;
    protected BackendRenderTarget renderTarget;

    protected Surface surface;

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

        if("false".equals(System.getProperty("skija.staticLoad"))){
            Library.load(); // Load Skia lib if it's not statically linked
        }
        context = DirectContext.makeGL();

        glfwSetWindowSizeCallback(window, (win, width, height) -> {
            createSurface();
        });
        createSurface();

        // TODO - user input

        glfwSwapInterval(1); // Use VSync

        ui = new ObsidianUI();
    }

    public void createSurface(){
        if(surface != null){
            renderTarget.close();
            surface.close();
        }

        renderTarget = BackendRenderTarget.makeGL(
                (int) (getWidth() / getXScale() * getDPI()),
                (int) (getHeight() / getYScale() * getDPI()),
                0,
                8,
                0,
                FramebufferFormat.GR_GL_RGBA8
        );

        surface = Surface.makeFromBackendRenderTarget(
                context,
                renderTarget,
                SurfaceOrigin.BOTTOM_LEFT,
                SurfaceColorFormat.RGBA_8888,
                ColorSpace.getDisplayP3(),
                new SurfaceProps(PixelGeometry.RGB_H)
        );
    }

    public void run(){
        glfwShowWindow(window);
        while(!glfwWindowShouldClose(window)){
            glfwPollEvents();
            ui.update(1.0/64.0); // Assume a constant refresh rate
            renderUI();
            context.flush();
            glfwSwapBuffers(window);
        }
    }

    public void renderUI(){
        var canvas = surface.getCanvas();
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