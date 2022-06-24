package myworld.client.obsidian;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class ObsidianPixels {

    protected final ByteBuffer buff;
    protected final IntBuffer intBuff;

    protected final int vPixels;
    protected final int wPixels;

    public ObsidianPixels(int wPixels, int vPixels){
        this.wPixels = wPixels;
        this.vPixels = vPixels;

        buff = ByteBuffer.allocate(wPixels * vPixels * 4);
        intBuff = buff.asIntBuffer();
    }

    public ByteBuffer rawBuffer() {
        return buff;
    }
    public IntBuffer pixelBuffer() {
        return intBuff;
    }
    public int getVPixels() {
        return vPixels;
    }
    public int getWPixels() {
        return wPixels;
    }
}
