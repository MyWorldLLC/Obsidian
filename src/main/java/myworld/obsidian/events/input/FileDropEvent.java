package myworld.obsidian.events.input;

import myworld.obsidian.input.InputManager;

public class FileDropEvent extends InputEvent {

    protected final String[] filePaths;

    public FileDropEvent(InputManager input, String[] filePaths){
        super(input);
        this.filePaths = filePaths;
    }

    public String[] getFilePaths(){
        return filePaths;
    }
}
