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

import com.sun.glass.ui.Pixels;
import com.sun.javafx.stage.EmbeddedWindow;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Window;
import myworld.obsidian.swapchain.Swapchain;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ObsidianUI {

    public static CompletableFuture<Boolean> init(){
        var future = new CompletableFuture<Boolean>();
        Platform.startup(() -> future.complete(true));
        Platform.setImplicitExit(false);
        return future;
    }

    public static void stop(){
        Platform.exit();
    }

    public static void inJfx(Runnable r){
        if(Platform.isFxApplicationThread()){
            r.run();
        }else{
            Platform.runLater(r);
        }
    }

    public static <T> CompletableFuture<T> inJfx(Supplier<T> s){
        var future = new CompletableFuture<T>();
        inJfx(() -> {
            future.complete(s.get());
        });
        return future;
    }

    protected final WindowInfo windowInfo;
    protected final CursorHandler cursorHandler;
    protected final JfxHost host;
    protected final InputHandler input;
    protected EmbeddedWindow window;
    protected final Group root;

    public ObsidianUI(WindowInfo windowInfo, String... cssStyles){
        this(windowInfo, null, cssStyles);
    }

    public ObsidianUI(WindowInfo windowInfo, CursorHandler cursorHandler, String... cssStyles){
        this.windowInfo = windowInfo;
        this.cursorHandler = cursorHandler;
        host = new JfxHost(this);
        input = new InputHandler(host);
        root = new Group();
        inJfx(() -> {
            window = new EmbeddedWindow(host);
            window.setScene(new Scene(root, windowInfo.getWidth(), windowInfo.getHeight()));
            window.getScene().setFill(Color.TRANSPARENT);
            window.getScene().getStylesheets().addAll(cssStyles);
            window.show();
        });
    }

    public ObsidianUI applyStyles(String... cssStyles){
        inJfx(() -> window.getScene().getStylesheets().addAll(cssStyles));
        return this;
    }

    public void reloadStyles(){
        inJfx(() -> {
            var styles = window.getScene().getStylesheets().toArray(new String[]{});
            window.hide();
            window.getScene().getStylesheets().clear();
            window.getScene().getStylesheets().addAll(styles);
            window.show();
        });
    }

    public void setRedrawListener(RedrawListener listener){
        host.setRedrawListener(listener);
    }

    public Window getJfxWindow(){
        return window;
    }

    public Group getRoot(){
        return root;
    }

    public void attach(Node node){
        inJfx(() -> root.getChildren().add(node));
    }

    public void detach(Node node){
        inJfx(() -> root.getChildren().remove(node));
    }

    public void detachAll(){
        inJfx(() -> root.getChildren().clear());
    }

    public void grabFocus(){
        inJfx(host::grabFocus);
    }

    public void loseFocus(){
        inJfx(host::ungrabFocus);
    }

    public void updateWindowInfo(){
        inJfx(host::updateLocation);
    }

    public WindowInfo getWindowInfo(){
        return windowInfo;
    }

    public CursorHandler getCursorHandler(){
        return cursorHandler;
    }

    public InputHandler getInputHandler(){
        return input;
    }

    public CompletableFuture<Swapchain.Format> getPixelFormat(){
        return inJfx(() -> switch (Pixels.getNativeFormat()) {
            case Pixels.Format.BYTE_ARGB -> Swapchain.Format.ARGB8;
            case Pixels.Format.BYTE_BGRA_PRE -> Swapchain.Format.BGRA8_PRE;
            default -> throw new IllegalStateException("Unsupported JavaFX pixel format");
        });
    }

}
