package example;

import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.display.Colors;
import myworld.obsidian.display.skin.ComponentSkin;
import myworld.obsidian.display.skin.StyleClass;
import myworld.obsidian.display.skin.VarType;
import myworld.obsidian.display.skin.builder.ComponentSkinBuilder;
import myworld.obsidian.display.skin.builder.RuleBuilder;
import myworld.obsidian.geometry.*;
import myworld.obsidian.scene.Component;
import myworld.obsidian.text.TextShadow;

import static myworld.obsidian.display.skin.StyleRule.constant;
import static myworld.obsidian.display.skin.StyleRules.*;

public class ExampleComponentSkin {

    public static ComponentSkin create(){
        return ComponentSkinBuilder.create("Example")
                .withParameter("text", VarType.STRING)
                .withStyle(
                        StyleClass.forLayer("background",
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, constant(
                                                RoundedRectangle.uniform(
                                                    new Rectangle(Distance.percentage(100), Distance.percentage(100)),
                                                    Distance.pixels(5)
                                                )))
                                        .withRule(COLOR, constant(ColorRGBA.of("#FFFFFF")))
                                        .withRule(BORDER_COLOR, constant(ColorRGBA.of("#666666FF")))
                                        .withRule(BORDER_WIDTH, constant(5))
                                        .withRule(BORDER_JOIN, constant("round"))
                                        .withRule(BORDER_CAP, constant("round"))
                                        .build()
                        )
                )
                .withStyle(
                        StyleClass.forLayerState("background", Component.FOCUSED_DATA_NAME,
                                RuleBuilder.create()
                                        .withRule(COLOR, constant(ColorRGBA.of("#AAAAAA")))
                                        .build()
                        )
                )
                .withStyle(
                        StyleClass.forLayer("svg",
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, constant(
                                                new SvgPath(
                                                        "M % % H % V % H % L % %",
                                                        SvgDist.horizontal(Distance.percentage(10)),
                                                        SvgDist.vertical(Distance.percentage(20)),
                                                        SvgDist.horizontal(Distance.percentage(90)),
                                                        SvgDist.vertical(Distance.percentage(90)),
                                                        SvgDist.horizontal(Distance.percentage(10)),
                                                        SvgDist.horizontal(Distance.percentage(10)),
                                                        SvgDist.vertical(Distance.percentage(20))
                                                )
                                        ))
                                        .withRule(BORDER_COLOR, constant(Colors.BLUE))
                                        .withRule(BORDER_WIDTH, constant(1))
                                        .build()
                        )
                )
                .withStyle(
                        StyleClass.forLayer("text",
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, constant("text"))
                                        .withRule(COLOR, constant(Colors.WHITE))
                                        .withRule(FONT_FAMILY, constant("Clear Sans"))
                                        .withRule(FONT_SIZE, constant(14))
                                        .withRule(TEXT_SHADOW, constant(
                                                new TextShadow(
                                                        new Move(Distance.pixels(-1), Distance.pixels(1)),
                                                        ColorRGBA.of("#000000"),
                                                        1f)
                                        ))
                                        .build()
                        )
                )
                .withStyle(
                        StyleClass.forLayerState("text", Component.HOVERED_DATA_NAME,
                                RuleBuilder.create()
                                        .withRule(COLOR, constant(Colors.GREEN))
                                        .build()
                        )
                )
                .build();
    }

}
