package myworld.obsidian.events;

import myworld.obsidian.input.InputManager;

public class CharacterEvent extends InputEvent {

    protected final char[] characters;

    public CharacterEvent(InputManager manager, char[] characters){
        super(manager);
        this.characters = characters;
    }

    public char[] getCharacters(){
        return characters;
    }
}
