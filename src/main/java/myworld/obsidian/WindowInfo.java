package myworld.client.obsidian;

public interface WindowInfo {

    int getPosX();
    int getPosY();
    int getHeight();
    int getWidth();

    default float getScaleX() {
        return 1;
    }

    default float getScaleY() {
        return 1;
    }

}
