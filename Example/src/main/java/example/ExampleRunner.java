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

package example;

import myworld.obsidian.ObsidianUI;
import myworld.obsidian.components.Button;
import myworld.obsidian.components.Checkbox;
import myworld.obsidian.components.layout.Pane;
import myworld.obsidian.components.layout.Stack;
import myworld.obsidian.components.text.TextDisplay;
import myworld.obsidian.components.text.TextField;
import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.display.skin.chipmunk.ChipmunkSkinLoader;
import myworld.obsidian.events.input.CharacterEvent;
import myworld.obsidian.events.input.KeyEvent;
import myworld.obsidian.events.input.MouseOverEvent;
import myworld.obsidian.events.scene.ButtonEvent;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.input.Key;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.input.MouseWheelAxis;
import myworld.obsidian.layout.Offsets;
import myworld.obsidian.scene.Component;
import myworld.obsidian.text.Text;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MAJOR;
import static org.lwjgl.glfw.GLFW.GLFW_CONTEXT_VERSION_MINOR;
import static org.lwjgl.glfw.GLFW.GLFW_DEPTH_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_FALSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_APOSTROPHE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSLASH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_CAPS_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_COMMA;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_END;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_EQUAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F10;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F11;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F12;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F13;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F14;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F15;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F16;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F17;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F18;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F19;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F20;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F21;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F22;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F23;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F24;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F25;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_GRAVE_ACCENT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_HOME;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_INSERT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_8;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_9;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ADD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DECIMAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_DIVIDE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_ENTER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_EQUAL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_MULTIPLY;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_KP_SUBTRACT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MENU;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_MINUS;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_NUM_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_DOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAGE_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PAUSE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PERIOD;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_PRINT_SCREEN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_ALT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_BRACKET;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_CONTROL;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SUPER;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SCROLL_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SEMICOLON;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SLASH;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_SPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_TAB;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UNKNOWN;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_UP;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_WORLD_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_WORLD_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_MIDDLE;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_CORE_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_FORWARD_COMPAT;
import static org.lwjgl.glfw.GLFW.GLFW_OPENGL_PROFILE;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_REPEAT;
import static org.lwjgl.glfw.GLFW.GLFW_SAMPLES;
import static org.lwjgl.glfw.GLFW.GLFW_STENCIL_BITS;
import static org.lwjgl.glfw.GLFW.GLFW_VISIBLE;
import static org.lwjgl.glfw.GLFW.glfwCreateWindow;
import static org.lwjgl.glfw.GLFW.glfwDefaultWindowHints;
import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;
import static org.lwjgl.glfw.GLFW.glfwGetWindowContentScale;
import static org.lwjgl.glfw.GLFW.glfwGetWindowSize;
import static org.lwjgl.glfw.GLFW.glfwInit;
import static org.lwjgl.glfw.GLFW.glfwMakeContextCurrent;
import static org.lwjgl.glfw.GLFW.glfwPollEvents;
import static org.lwjgl.glfw.GLFW.glfwSetCharCallback;
import static org.lwjgl.glfw.GLFW.glfwSetCursorPosCallback;
import static org.lwjgl.glfw.GLFW.glfwSetKeyCallback;
import static org.lwjgl.glfw.GLFW.glfwSetMouseButtonCallback;
import static org.lwjgl.glfw.GLFW.glfwSetScrollCallback;
import static org.lwjgl.glfw.GLFW.glfwSetWindowSizeCallback;
import static org.lwjgl.glfw.GLFW.glfwShowWindow;
import static org.lwjgl.glfw.GLFW.glfwSwapBuffers;
import static org.lwjgl.glfw.GLFW.glfwSwapInterval;
import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFW.glfwWindowHint;
import static org.lwjgl.glfw.GLFW.glfwWindowShouldClose;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.system.MemoryUtil.NULL;

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

        GL.createCapabilities();
        glfwSwapInterval(1); // Use VSync

        ui = ObsidianUI.createForGL(getRenderWidth(), getRenderHeight(), 4, 0);
        ui.registerSkin(ChipmunkSkinLoader.loadFromClasspath(ChipmunkSkinLoader.DEFAULT_SKIN));
        ui.useSkin("Obsidian");
        ui.clipboard().set(new GLFWClipboard(window));
        //ui.getDisplay().enableRenderDebug(Colors.RED);

        ui.clearColor().set(ColorRGBA.of("#EEEEEE"));

        registerInputListeners(window);

        var layout = new ExampleLayout();
        ui.getRoot().addChild(layout);

        var example = new Component();
        example.styleName().set("Example");
        example.layout().preferredSize(Distance.pixels(100), Distance.pixels(50));
        layout.left().addChild(example);

        example.renderVars().put("text", () -> new Text("Hello, World!", ui.getStyle("ExampleText")));

        example.dispatcher().subscribe(MouseOverEvent.class, evt -> evt.entered(example), evt -> {
            System.out.println("Mouse entered");
        });

        example.dispatcher().subscribe(MouseOverEvent.class, evt -> evt.exited(example), evt -> {
            System.out.println("Mouse exited");
        });

        example.dispatcher().subscribe(MouseOverEvent.class, evt -> evt.isHovering(example), evt -> {
            System.out.println("(%d, %d)".formatted(evt.getX(), evt.getY()));
        });

        example.dispatcher().subscribe(CharacterEvent.class, evt -> {
            System.out.println("Received characters: " + String.copyValueOf(evt.getCharacters()));
        });

        example.dispatcher().subscribe(KeyEvent.class, KeyEvent::isDown, evt -> {
            System.out.println("Key: " + evt.getKey());
        });

        var textField = TextField.password('*');
        textField.layout().preferredSize(Distance.pixels(100), Distance.pixels(50));
        textField.insert("Foo");
        layout.center().addChild(textField);

        var label = new TextDisplay();
        label.layout().preferredSize(Distance.pixels(100), Distance.pixels(100));
        label.text().set("Hello, World!");
        layout.left().addChild(label);

        var button = Button.textButton(Text.styled("Hello, Buttons!", ui.getStyle("ExampleText")));
        button.layout().margin().set(new Offsets(Distance.pixels(10)));
        button.addButtonListener(ButtonEvent::isPressed, evt -> System.out.println("Pressed!"));
        button.addButtonListener(ButtonEvent::isClicked, evt -> System.out.println("Clicked!"));
        button.addButtonListener(ButtonEvent::isReleased, evt -> System.out.println("Released!"));
        layout.right().addChild(button);

        var checkbox = new Checkbox()
                .onChange(evt -> System.out.println("Checked: " + evt.isChecked()));
        layout.right().addChild(checkbox);

        var topPane = new Pane();
        var topButton = Button
                .textButton(Text.plain("Top"))
                .onClick(e -> System.out.println("Top clicked"));
        topPane.addChild(topButton);

        var bottomPane = new Pane();
        var bottomButton = Button
                .textButton(Text.plain("Bottom"))
                .onClick(e -> System.out.println("Bottom clicked"));
        bottomPane.addChild(bottomButton);

        var stack = new Stack()
                .withChildren(topPane, bottomPane);
        stack.layout().clampedSize(250, 250);

        layout.center().addChild(stack);

        ui.requestFocus(textField);

    }

    public int getRenderWidth(){
        return (int) (getWidth() / getXScale() * getDPI());
    }

    public int getRenderHeight(){
        return (int) (getHeight() / getYScale() * getDPI());
    }

    public void createSurface(){
        ui.display().ifSet(d -> d.resize(getRenderWidth(), getRenderHeight()));
        //ui.getDisplay().enableRenderDebug(Colors.RED);
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
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            e.printStackTrace();
        }
    }

    protected void registerInputListeners(long window){
        glfwSetCharCallback(window, this::charCallback);

        glfwSetCursorPosCallback(window, this::mouseMoveCallback);

        glfwSetMouseButtonCallback(window, this::mouseButtonCallback);

        glfwSetScrollCallback(window, this::scrollCallback);

        glfwSetKeyCallback(window, this::keyCallback);
    }

    protected void charCallback(long window, int codePoint){
        ui.getInput().fireCharacterEvent(Character.toChars(codePoint));
    }

    protected void mouseMoveCallback(long window, double x, double y){
        ui.getInput().fireMouseMoveEvent((int)x, (int)y);
    }

    protected void mouseButtonCallback(long window, int glfwButton, int action, int mods){
        // Ignore modifiers, because Obsidian already tracks those key states
        var isPressed = action == GLFW_PRESS;
        var button = switch (glfwButton){
            case GLFW_MOUSE_BUTTON_LEFT -> MouseButton.PRIMARY;
            case GLFW_MOUSE_BUTTON_MIDDLE -> MouseButton.MIDDLE;
            case GLFW_MOUSE_BUTTON_RIGHT -> MouseButton.SECONDARY;
            default -> MouseButton.NONE;
        };

        int x = 0;
        int y = 0;
        var pos = ui.getInput().getMousePosition();
        if(pos != null){
            x = pos.x();
            y = pos.y();
        }
        ui.getInput().fireMouseButtonEvent(button, isPressed, x, y);
    }

    protected void scrollCallback(long window, double xScroll, double yScroll){
        int x = 0;
        int y = 0;
        var pos = ui.getInput().getMousePosition();
        if(pos != null){
            x = pos.x();
            y = pos.y();
        }

        if(xScroll != 0.0){
            ui.getInput().fireMouseWheelEvent(MouseWheelAxis.HORIZONTAL, x, y, (float)xScroll);
        }

        if(yScroll != 0.0){
            ui.getInput().fireMouseWheelEvent(MouseWheelAxis.VERTICAL, x, y, (float)yScroll);
        }
    }

    protected void keyCallback(long window, int glfwKey, int scancode, int action, int mods){
        // Ignore modifiers because Obsidian already tracks those
        var isDown = action == GLFW_PRESS || action == GLFW_REPEAT;
        var key = switch (glfwKey){
            case GLFW_KEY_UNKNOWN -> Key.UNKNOWN;
            case GLFW_KEY_SPACE -> Key.SPACE;
            case GLFW_KEY_APOSTROPHE -> Key.APOSTROPHE;
            case GLFW_KEY_COMMA -> Key.COMMA;
            case GLFW_KEY_MINUS -> Key.MINUS;
            case GLFW_KEY_PERIOD -> Key.PERIOD;
            case GLFW_KEY_SLASH -> Key.SLASH;
            case GLFW_KEY_0 -> Key.KEY_0;
            case GLFW_KEY_1 -> Key.KEY_1;
            case GLFW_KEY_2 -> Key.KEY_2;
            case GLFW_KEY_3 -> Key.KEY_3;
            case GLFW_KEY_4 -> Key.KEY_4;
            case GLFW_KEY_5 -> Key.KEY_5;
            case GLFW_KEY_6 -> Key.KEY_6;
            case GLFW_KEY_7 -> Key.KEY_7;
            case GLFW_KEY_8 -> Key.KEY_8;
            case GLFW_KEY_9 -> Key.KEY_9;
            case GLFW_KEY_SEMICOLON -> Key.SEMICOLON;
            case GLFW_KEY_EQUAL -> Key.EQUAL;
            case GLFW_KEY_A -> Key.KEY_A;
            case GLFW_KEY_B -> Key.KEY_B;
            case GLFW_KEY_C -> Key.KEY_C;
            case GLFW_KEY_D -> Key.KEY_D;
            case GLFW_KEY_E -> Key.KEY_E;
            case GLFW_KEY_F -> Key.KEY_F;
            case GLFW_KEY_G -> Key.KEY_G;
            case GLFW_KEY_H -> Key.KEY_H;
            case GLFW_KEY_I -> Key.KEY_I;
            case GLFW_KEY_J -> Key.KEY_J;
            case GLFW_KEY_K -> Key.KEY_K;
            case GLFW_KEY_L -> Key.KEY_L;
            case GLFW_KEY_M -> Key.KEY_M;
            case GLFW_KEY_N -> Key.KEY_N;
            case GLFW_KEY_O -> Key.KEY_O;
            case GLFW_KEY_P -> Key.KEY_P;
            case GLFW_KEY_Q -> Key.KEY_Q;
            case GLFW_KEY_R -> Key.KEY_R;
            case GLFW_KEY_S -> Key.KEY_S;
            case GLFW_KEY_T -> Key.KEY_T;
            case GLFW_KEY_U -> Key.KEY_U;
            case GLFW_KEY_V -> Key.KEY_V;
            case GLFW_KEY_W -> Key.KEY_W;
            case GLFW_KEY_X -> Key.KEY_X;
            case GLFW_KEY_Y -> Key.KEY_Y;
            case GLFW_KEY_Z -> Key.KEY_Z;
            case GLFW_KEY_LEFT_BRACKET -> Key.LEFT_BRACKET;
            case GLFW_KEY_BACKSLASH -> Key.BACKSLASH;
            case GLFW_KEY_RIGHT_BRACKET -> Key.RIGHT_BRACKET;
            case GLFW_KEY_GRAVE_ACCENT -> Key.GRAVE_ACCENT;
            case GLFW_KEY_WORLD_1 -> Key.WORLD_1;
            case GLFW_KEY_WORLD_2 -> Key.WORLD_2;
            case GLFW_KEY_ESCAPE -> Key.ESCAPE;
            case GLFW_KEY_ENTER -> Key.ENTER;
            case GLFW_KEY_TAB -> Key.TAB;
            case GLFW_KEY_BACKSPACE -> Key.BACKSPACE;
            case GLFW_KEY_INSERT -> Key.INSERT;
            case GLFW_KEY_DELETE -> Key.DELETE;
            case GLFW_KEY_RIGHT -> Key.RIGHT;
            case GLFW_KEY_LEFT -> Key.LEFT;
            case GLFW_KEY_DOWN -> Key.DOWN;
            case GLFW_KEY_UP -> Key.UP;
            case GLFW_KEY_PAGE_UP -> Key.PAGE_UP;
            case GLFW_KEY_PAGE_DOWN -> Key.PAGE_DOWN;
            case GLFW_KEY_HOME -> Key.HOME;
            case GLFW_KEY_END -> Key.END;
            case GLFW_KEY_CAPS_LOCK -> Key.CAPS_LOCK;
            case GLFW_KEY_SCROLL_LOCK -> Key.SCROLL_LOCK;
            case GLFW_KEY_NUM_LOCK -> Key.NUM_LOCK;
            case GLFW_KEY_PRINT_SCREEN -> Key.PRINT_SCREEN;
            case GLFW_KEY_PAUSE -> Key.PAUSE;
            case GLFW_KEY_F1 -> Key.F1;
            case GLFW_KEY_F2 -> Key.F2;
            case GLFW_KEY_F3 -> Key.F3;
            case GLFW_KEY_F4 -> Key.F4;
            case GLFW_KEY_F5 -> Key.F5;
            case GLFW_KEY_F6 -> Key.F6;
            case GLFW_KEY_F7 -> Key.F7;
            case GLFW_KEY_F8 -> Key.F8;
            case GLFW_KEY_F9 -> Key.F9;
            case GLFW_KEY_F10 -> Key.F10;
            case GLFW_KEY_F11 -> Key.F11;
            case GLFW_KEY_F12 -> Key.F12;
            case GLFW_KEY_F13 -> Key.F13;
            case GLFW_KEY_F14 -> Key.F14;
            case GLFW_KEY_F15 -> Key.F15;
            case GLFW_KEY_F16 -> Key.F16;
            case GLFW_KEY_F17 -> Key.F17;
            case GLFW_KEY_F18 -> Key.F18;
            case GLFW_KEY_F19 -> Key.F19;
            case GLFW_KEY_F20 -> Key.F20;
            case GLFW_KEY_F21 -> Key.F21;
            case GLFW_KEY_F22 -> Key.F22;
            case GLFW_KEY_F23 -> Key.F23;
            case GLFW_KEY_F24 -> Key.F24;
            case GLFW_KEY_F25 -> Key.F25;
            case GLFW_KEY_KP_0 -> Key.KP_0;
            case GLFW_KEY_KP_1 -> Key.KP_1;
            case GLFW_KEY_KP_2 -> Key.KP_2;
            case GLFW_KEY_KP_3 -> Key.KP_3;
            case GLFW_KEY_KP_4 -> Key.KP_4;
            case GLFW_KEY_KP_5 -> Key.KP_5;
            case GLFW_KEY_KP_6 -> Key.KP_6;
            case GLFW_KEY_KP_7 -> Key.KP_7;
            case GLFW_KEY_KP_8 -> Key.KP_8;
            case GLFW_KEY_KP_9 -> Key.KP_9;
            case GLFW_KEY_KP_DECIMAL -> Key.KP_DECIMAL;
            case GLFW_KEY_KP_DIVIDE -> Key.KP_DIVIDE;
            case GLFW_KEY_KP_MULTIPLY -> Key.KP_MULTIPLY;
            case GLFW_KEY_KP_SUBTRACT -> Key.KP_SUBTRACT;
            case GLFW_KEY_KP_ADD -> Key.KP_ADD;
            case GLFW_KEY_KP_ENTER -> Key.KP_ENTER;
            case GLFW_KEY_KP_EQUAL -> Key.KP_EQUAL;
            case GLFW_KEY_LEFT_SHIFT -> Key.LEFT_SHIFT;
            case GLFW_KEY_LEFT_CONTROL -> Key.LEFT_CONTROL;
            case GLFW_KEY_LEFT_ALT -> Key.LEFT_ALT;
            case GLFW_KEY_LEFT_SUPER -> Key.LEFT_META;
            case GLFW_KEY_RIGHT_SHIFT -> Key.RIGHT_SHIFT;
            case GLFW_KEY_RIGHT_CONTROL -> Key.RIGHT_CONTROL;
            case GLFW_KEY_RIGHT_ALT -> Key.RIGHT_ALT;
            case GLFW_KEY_RIGHT_SUPER -> Key.RIGHT_META;
            case GLFW_KEY_MENU -> Key.MENU;
                default -> Key.UNKNOWN;
        };

        ui.getInput().fireKeyEvent(key, isDown);
    }


}