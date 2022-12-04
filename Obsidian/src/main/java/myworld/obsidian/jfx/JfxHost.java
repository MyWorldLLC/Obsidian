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

package myworld.obsidian.jfx;

import com.sun.javafx.cursor.CursorFrame;
import com.sun.javafx.cursor.ImageCursorFrame;
import com.sun.javafx.cursor.StandardCursorFrame;
import com.sun.javafx.embed.AbstractEvents;
import com.sun.javafx.embed.EmbeddedSceneInterface;
import com.sun.javafx.embed.EmbeddedStageInterface;
import com.sun.javafx.embed.HostInterface;
import com.sun.javafx.tk.Toolkit;
import javafx.scene.image.PixelFormat;
import myworld.obsidian.ObsidianCursor;
import myworld.obsidian.ObsidianPixels;
import myworld.obsidian.jfx.swapchain.Swapchain;

import java.util.concurrent.atomic.AtomicReference;

public class JfxHost implements HostInterface {

    protected final ObsidianJfxUI ui;

    protected final AtomicReference<RedrawListener> redrawListener;
    protected Swapchain swapchain;

    protected EmbeddedStageInterface stage;
    protected EmbeddedSceneInterface scene;

    protected int prefWidth;
    protected int prefHeight;

    protected boolean focused;

    public JfxHost(ObsidianJfxUI ui){
        this.ui = ui;
        redrawListener = new AtomicReference<>();
        prefWidth = -1;
        prefHeight = -1;
    }

    public void setRedrawListener(RedrawListener listener){
        redrawListener.set(listener);
    }

    public RedrawListener getRedrawListener(){
        return redrawListener.get();
    }

    @Override
    public void setEmbeddedStage(EmbeddedStageInterface embeddedStage) {
        stage = embeddedStage;

        var window = ui.getWindowInfo();
        stage.setSize(window.getWidth(), window.getHeight());
        stage.setLocation(window.getPosX(), window.getPosY());
    }

    public EmbeddedStageInterface getStage(){
        return stage;
    }

    @Override
    public void setEmbeddedScene(EmbeddedSceneInterface embeddedScene) {
        scene = embeddedScene;

        var window = ui.getWindowInfo();
        scene.setSize(window.getWidth(), window.getHeight());
        scene.setPixelScaleFactors(window.getScaleX(), window.getScaleY());

    }

    public EmbeddedSceneInterface getScene(){
        return scene;
    }

    @Override
    public boolean requestFocus() {
        ui.grabFocus();
        return true;
    }

    @Override
    public boolean traverseFocusOut(boolean forward) {
        return false;
    }

    @Override
    public void repaint() {
        if(scene == null){
            return;
        }

        if(swapchain == null){
            createSwapchain();
        }

        var pixels = swapchain.beginWrite();
        // getPixels() will handle the buffer state, no need to prep buffer for writing
        scene.getPixels(pixels.pixelBuffer(), pixels.getWPixels(), pixels.getVPixels());
        swapchain.finishWrite(pixels);
    }

    public int getScreenX(int x){
        return ui.getWindowInfo().getPosX() + x;
    }

    public int getScreenY(int y){
        return ui.getWindowInfo().getPosY() + y;
    }

    protected void updateLocation(){
        var window = ui.getWindowInfo();
        if(stage != null){
            stage.setLocation(window.getPosX(), window.getPosY());
        }
        if(scene != null){
            scene.setPixelScaleFactors(window.getScaleX(), window.getScaleY());
        }
        setPreferredSize(window.getWidth(), window.getHeight());
    }

    @Override
    public void setPreferredSize(int width, int height) {
        prefWidth = width;
        prefHeight = height;
        if(stage != null){
            stage.setSize(width, height);
        }
        if(scene != null){
            scene.setSize(width, height);
        }

        if(swapchain == null || width != swapchain.getWPixels() || height != swapchain.getVPixels()){
            createSwapchain();
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        grabFocus();
    }

    @Override
    public void setCursor(CursorFrame cursorFrame) {
        var cursorHandler = ui.getCursorHandler();
        if(cursorHandler != null){

            if(cursorFrame instanceof StandardCursorFrame standardCursor){
                cursorHandler.cursorRequested(ObsidianCursor.ofType(standardCursor.getCursorType().name()));
            }else if(cursorFrame instanceof ImageCursorFrame imageCursor){
                var srcImage = Toolkit.getImageAccessor().fromPlatformImage(imageCursor.getPlatformImage());
                var dstImage = new ObsidianPixels((int)Math.ceil(srcImage.getWidth()), (int)Math.ceil(srcImage.getHeight()));

                var format = PixelFormat.getIntArgbInstance();
                srcImage.getPixelReader().getPixels(0, 0, dstImage.getWPixels(), dstImage.getVPixels(), format, dstImage.pixelBuffer(), dstImage.getWPixels());

                cursorHandler.cursorRequested(ObsidianCursor.ofImage(dstImage, imageCursor.getHotspotX(), imageCursor.getHotspotY()));
            }
        }
    }

    @Override
    public boolean grabFocus() {

        if(focused || stage == null){
            return true;
        }

        stage.setFocused(true, AbstractEvents.FOCUSEVENT_ACTIVATED);
        focused = true;
        return true;
    }

    @Override
    public void ungrabFocus() {
        if(!focused || stage == null){
            return;
        }

        stage.setFocused(false, AbstractEvents.FOCUSEVENT_DEACTIVATED);
        focused = false;
    }

    protected void createSwapchain() {

        Swapchain.Format format = ui.getPixelFormat().join();

        swapchain = new Swapchain(format,
                (int) Math.ceil(getWidth() * ui.getWindowInfo().getScaleX()),
                (int) Math.ceil(getHeight() * ui.getWindowInfo().getScaleY()));

        swapchain.notifyOnWrite(() -> {
            var listener = redrawListener.get();
            if (listener != null) {
                var pixels = swapchain.beginRead();
                pixels.pixelBuffer().rewind(); // Prepare buffer for reading
                listener.onRedraw(pixels, () -> swapchain.finishRead());
            }
        });
    }

    public int getWidth(){
        return prefWidth != -1 ? prefWidth : ui.getWindowInfo().getWidth();
    }

    public int getHeight(){
        return prefHeight != -1 ? prefHeight : ui.getWindowInfo().getHeight();
    }
}
