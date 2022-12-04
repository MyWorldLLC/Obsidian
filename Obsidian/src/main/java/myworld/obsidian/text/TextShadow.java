package myworld.obsidian.text;

import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.geometry.Move;

public record TextShadow(Move offset, ColorRGBA color, float blurSigma) {}
