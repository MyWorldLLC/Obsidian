/*
 *    Copyright 2022 MyWorld, LLC
 *
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

package myworld.obsidian;

import com.sun.javafx.embed.AbstractEvents;
import javafx.scene.input.KeyCode;
import myworld.obsidian.input.KeyChars;
import myworld.obsidian.input.MouseButton;
import myworld.obsidian.input.MouseWheelAxis;
import myworld.obsidian.input.InputStates;

public class InputHandler {

    public static final int DEFAULT_MOUSE_POPUP_TRIGGER = MouseButton.SECONDARY.fxButton();

    protected final JfxHost host;
    protected final InputStates keyStates;
    protected final InputStates mouseButtonStates;
    protected final KeyChars keyChars;

    protected int mousePopupTrigger;

    protected double mouseWheelXMultiplier;
    protected double mouseWheelYMultiplier;
    protected boolean mouseInertia;

    public InputHandler(JfxHost host){
        this.host = host;

        keyStates = new InputStates();

        mouseButtonStates = new InputStates();

        keyChars = new KeyChars();

        mousePopupTrigger = DEFAULT_MOUSE_POPUP_TRIGGER;


        mouseWheelXMultiplier = 50;
        mouseWheelYMultiplier = 50;
        mouseInertia = false;
    }

    public void keyEvent(int keyCode, char keyChar, boolean isDown, boolean repeating){

        var wasDown = keyStates.set(keyCode, isDown);

        if(isDown && repeating){
            dispatchKeyEvent(AbstractEvents.KEYEVENT_TYPED, keyCode, keyChar);
        }else if(isDown){
            dispatchKeyEvent(AbstractEvents.KEYEVENT_PRESSED, keyCode, keyChar);
            dispatchKeyEvent(AbstractEvents.KEYEVENT_TYPED, keyCode, keyChar);
        }else if(wasDown){
            dispatchKeyEvent(AbstractEvents.KEYEVENT_RELEASED, keyCode, keyChar);
        }

    }

    public void mouseWheelEvent(MouseWheelAxis axis, double wheelDelta, double wheelTotal, int x, int y){
        dispatchScrollEvent(axis, wheelDelta, wheelTotal, getMouseWheelMultiplier(axis), x, y, mouseInertia);
    }

    public void mouseButtonEvent(MouseButton button, boolean isDown, int x, int y){
        dispatchMouseEvent(button, isDown, x, y);
    }

    public void mouseMoveEvent(int x, int y){
        dispatchMouseEvent(MouseButton.NONE, false, x, y);
    }

    protected void dispatchMouseEvent(MouseButton button, boolean isDown, int x, int y){
        int type;
        if(!MouseButton.NONE.equals(button)){
            mouseButtonStates.set(button.fxButton(), isDown);
            type = isDown ? AbstractEvents.MOUSEEVENT_PRESSED : AbstractEvents.MOUSEEVENT_RELEASED;
        }else if(mouseButtonStates.get(MouseButton.PRIMARY.fxButton())
                || mouseButtonStates.get(MouseButton.MIDDLE.fxButton())
                || mouseButtonStates.get(MouseButton.SECONDARY.fxButton())){
            type = AbstractEvents.MOUSEEVENT_DRAGGED;
        }else{
            type = AbstractEvents.MOUSEEVENT_MOVED;
        }

        var scene = host.getScene();
        if (scene != null) {
            scene.mouseEvent(
                    type,
                    button.fxButton(),
                    mouseButtonStates.get(MouseButton.PRIMARY.fxButton()),
                    mouseButtonStates.get(MouseButton.MIDDLE.fxButton()),
                    mouseButtonStates.get(MouseButton.SECONDARY.fxButton()),
                    mouseButtonStates.get(MouseButton.BACK.fxButton()),
                    mouseButtonStates.get(MouseButton.FORWARD.fxButton()),
                    x, y, host.getScreenX(x), host.getScreenY(y),
                    keyStates.get(KeyCode.SHIFT.getCode()),
                    keyStates.get(KeyCode.CONTROL.getCode()),
                    keyStates.get(KeyCode.ALT.getCode()),
                    keyStates.get(KeyCode.META.getCode()),
                    button.fxButton() == getMousePopupTrigger()
            );
        }
    }

    protected void dispatchKeyEvent(int type, int keyCode, char keyChar){
        var scene = host.getScene();
        if(scene != null){

            int modifiers = 0;
            if(keyStates.get(KeyCode.SHIFT.getCode())){
                modifiers |= AbstractEvents.MODIFIER_SHIFT;
            }
            if(keyStates.get(KeyCode.CONTROL.getCode())){
                modifiers |= AbstractEvents.MODIFIER_CONTROL;
            }
            if(keyStates.get(KeyCode.ALT.getCode())){
                modifiers |= AbstractEvents.MODIFIER_ALT;
            }
            if(keyStates.get(KeyCode.META.getCode())){
                modifiers |= AbstractEvents.MODIFIER_META;
            }
            if(keyChar == '\n'){
                keyChar = '\r'; // JFX requires converting LF to CR
            }
            scene.keyEvent(type, keyCode, keyChars.charsFor(keyChar), modifiers);
        }
    }

    protected void dispatchScrollEvent(MouseWheelAxis axis, double wheelDelta, double wheelTotal, double multiplier, int x, int y, boolean inertia){
        var scene = host.getScene();
        if(scene != null){

            int type = axis.fxAxis();
            double scrollX = 0,
                    scrollY = 0,
                    totalScrollX = 0,
                    totalScrollY = 0,
                    xMultiplier = 0,
                    yMultiplier = 0;

            switch (axis){
                case HORIZONTAL -> {
                    scrollX = wheelDelta;
                    totalScrollX = wheelTotal;
                    xMultiplier = multiplier;
                }
                case VERTICAL -> {
                    scrollY = wheelDelta;
                    totalScrollY = wheelTotal;
                    yMultiplier = multiplier;
                }
            }

            scene.scrollEvent(
                    type,
                    scrollX,
                    scrollY,
                    totalScrollX,
                    totalScrollY,
                    xMultiplier,
                    yMultiplier,
                    x, y, host.getScreenX(x), host.getScreenY(y),
                    keyStates.get(KeyCode.SHIFT.getCode()),
                    keyStates.get(KeyCode.CONTROL.getCode()),
                    keyStates.get(KeyCode.ALT.getCode()),
                    keyStates.get(KeyCode.META.getCode()),
                    inertia
            );
        }
    }

    public void setMousePopupTrigger(int button){
        mousePopupTrigger = button;
    }

    public int getMousePopupTrigger(){
        return mousePopupTrigger;
    }

    public void setMouseWheelMultipliers(double xMultiplier, double yMultiplier){
        mouseWheelXMultiplier = xMultiplier;
        mouseWheelYMultiplier = yMultiplier;
    }

    public double getMouseWheelXMultiplier(){
        return mouseWheelXMultiplier;
    }

    public double getMouseWheelYMultiplier(){
        return mouseWheelYMultiplier;
    }

    public double getMouseWheelMultiplier(MouseWheelAxis axis){
        return switch (axis){
            case HORIZONTAL -> mouseWheelXMultiplier;
            case VERTICAL -> mouseWheelYMultiplier;
        };
    }

    public void useMouseInertia(boolean inertia){
        mouseInertia = inertia;
    }

    public boolean isUsingMouseInertia(){
        return mouseInertia;
    }

    protected boolean getKeyState(int keyCode){
        return keyStates.get(keyCode, false);
    }

    protected void setKeyState(int keyCode, boolean state){
        keyStates.set(keyCode, state);
    }

}
