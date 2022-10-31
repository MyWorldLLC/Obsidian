package myworld.obsidian.input;

public interface ClipboardHandler {

    void toClipboard(String s);
    String fromClipboard();

    static ClipboardHandler droppingClipboard(){
        return new ClipboardHandler() {
            @Override
            public void toClipboard(String s) {}

            @Override
            public String fromClipboard() {
                return null;
            }
        };
    }

}
