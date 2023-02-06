module myworld.obsidian {
    requires io.github.humbleui.skija.shared;
    requires io.github.humbleui.types;
    requires org.lwjgl.yoga;

    exports myworld.obsidian;
    exports myworld.obsidian.components;
    exports myworld.obsidian.components.layout;
    exports myworld.obsidian.components.text;
    exports myworld.obsidian.display;
    exports myworld.obsidian.display.skin;
    exports myworld.obsidian.display.skin.builder;
    exports myworld.obsidian.display.skin.obsidian;
    exports myworld.obsidian.scene.effects;
    exports myworld.obsidian.events;
    exports myworld.obsidian.events.dispatch;
    exports myworld.obsidian.events.input;
    exports myworld.obsidian.events.scene;
    exports myworld.obsidian.geometry;
    exports myworld.obsidian.input;
    exports myworld.obsidian.layout;
    exports myworld.obsidian.properties;
    exports myworld.obsidian.scene;
    exports myworld.obsidian.scene.layout;
    exports myworld.obsidian.text;
    exports myworld.obsidian.util;

}