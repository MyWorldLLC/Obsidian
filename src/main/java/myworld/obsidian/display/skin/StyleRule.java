package myworld.obsidian.display.skin;

import java.util.function.Supplier;

public record StyleRule(String name, Supplier<?>... args) {
}
