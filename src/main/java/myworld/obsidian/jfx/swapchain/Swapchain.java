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

package myworld.obsidian.jfx.swapchain;

import myworld.obsidian.ObsidianPixels;

import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;

public class Swapchain {

    public enum Format {
        ARGB8, BGRA8_PRE
    }

    private static final int READ = 0;
    private static final int READABLE = 1;
    private static final int WRITEABLE = 2;

    protected final Format format;
    protected final int wPixels;
    protected final int vPixels;

    protected final ReentrantLock lock;
    protected final AtomicReference<Runnable> writeListener;

    protected final ObsidianPixels[] chain;

    public Swapchain(Format format, int wPixels, int vPixels){
        lock = new ReentrantLock();
        writeListener = new AtomicReference<>();
        chain = new ObsidianPixels[]{
            new ObsidianPixels(wPixels, vPixels),
            new ObsidianPixels(wPixels, vPixels),
            new ObsidianPixels(wPixels, vPixels)
        };
        this.format = format;
        this.wPixels = wPixels;
        this.vPixels = vPixels;
    }

    public void notifyOnWrite(Runnable r){
        writeListener.set(r);
    }

    public Format getFormat(){
        return format;
    }

    public int getWPixels() {
        return wPixels;
    }

    public int getVPixels() {
        return vPixels;
    }

    public ObsidianPixels beginWrite(){
        // Get the buffer in the write position in the chain
        return getBuffer(WRITEABLE);
    }

    public void finishWrite(ObsidianPixels pixels){

        atomically(() -> {
            // Reclaim the formerly readable pixels for writing,
            // and mark this current set readable
            swapBuffers(READABLE, WRITEABLE);
            setBufferState(READABLE, pixels);
        });

        Runnable listener = writeListener.get();
        if(listener != null){
            listener.run();
        }
    }

    public ObsidianPixels beginRead(){
        swapBuffers(READABLE, READ);
        return getBuffer(READ);
    }

    public void finishRead(){
        swapBuffers(READ, WRITEABLE);
    }

    private void swapBuffers(int first, int second){
        atomically(() -> {
            var firstBuffer = chain[first];
            var secondBuffer = chain[second];
            chain[first] = secondBuffer;
            chain[second] = firstBuffer;
        });
    }

    private void setBufferState(int index, ObsidianPixels pixels){
        atomically(() -> chain[index] = pixels);
    }

    private ObsidianPixels getBuffer(int index){
        try{
            lock.lock();
            return chain[index];
        }finally{
            lock.unlock();
        }
    }

    private void atomically(Runnable r){
        try{
            lock.lock();
            r.run();
        }finally{
            lock.unlock();
        }
    }

}
