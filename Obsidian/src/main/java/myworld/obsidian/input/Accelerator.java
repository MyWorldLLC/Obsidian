package myworld.obsidian.input;

import myworld.obsidian.events.input.KeyEvent;

import java.util.function.Consumer;

public record Accelerator(Key[] keys, Consumer<KeyEvent> listener) {}
