package myworld.obsidian.display.skin.obsidian;

import myworld.obsidian.components.Button;
import myworld.obsidian.components.Checkbox;
import myworld.obsidian.components.layout.Viewport;
import myworld.obsidian.components.text.EditableTextDisplay;
import myworld.obsidian.components.text.TextDisplay;
import myworld.obsidian.display.ColorRGBA;
import myworld.obsidian.display.Colors;
import myworld.obsidian.display.OverflowModes;
import myworld.obsidian.display.skin.*;
import myworld.obsidian.display.skin.builder.ComponentSkinBuilder;
import myworld.obsidian.display.skin.builder.RuleBuilder;
import myworld.obsidian.display.skin.builder.UISkinBuilder;
import myworld.obsidian.geometry.Distance;
import myworld.obsidian.geometry.Ellipse;
import myworld.obsidian.geometry.Move;
import myworld.obsidian.geometry.Rectangle;
import myworld.obsidian.scene.Component;
import myworld.obsidian.text.TextShadow;

import static myworld.obsidian.display.skin.StyleRules.*;
import static myworld.obsidian.display.skin.StyleRule.*;

import java.util.List;

public class ObsidianSkin {

    public static final String OBSIDIAN_SKIN_NAME = "Obsidian";

    public static final ColorRGBA HIGHLIGHT_COLOR = ColorRGBA.of("#25EFF988");
    public static final ColorRGBA FOCUS_COLOR = ColorRGBA.of("#4CCAF4");

    public static UISkin create(){
        return create(ObsidianSkin.class::getResourceAsStream);
    }

    public static UISkin create(ResourceResolver resolver){
        return UISkinBuilder.create(OBSIDIAN_SKIN_NAME, resolver)
                .withComponent(root())
                .withComponent(example())
                .withComponent(button())
                .withComponent(textDisplay())
                .withComponent(editableTextDisplay())
                .withComponent(textField())
                .withComponent(checkbox())
                .withComponent(viewport())
                .withStyleClass(
                        StyleClass.forName("ExampleText",
                                RuleBuilder.create()
                                        .withRule(COLOR, constant(ColorRGBA.of("#00FF00")))
                                        .build()
                        )
                )
                .withStyleClass(
                        StyleClass.forName("DefaultFocus",
                                RuleBuilder.create()
                                        .withRule(BORDER_COLOR, constant(ColorRGBA.of("#4CCAF4")))
                                        .withRule(BORDER_WIDTH, constant(1.5f))
                                        .withRule(BORDER_JOIN, constant("round"))
                                        .withRule(BORDER_CAP, constant("round"))
                                        .build()
                        )
                )
                .withFonts(List.of(
                        "/myworld/obsidian/skin/fonts/clear-sans/ClearSans-Bold.ttf",
                        "/myworld/obsidian/skin/fonts/clear-sans/ClearSans-BoldItalic.ttf",
                        "/myworld/obsidian/skin/fonts/clear-sans/ClearSans-Italic.ttf",
                        "/myworld/obsidian/skin/fonts/clear-sans/ClearSans-Light.ttf",
                        "/myworld/obsidian/skin/fonts/clear-sans/ClearSans-Bold.ttf",
                        "/myworld/obsidian/skin/fonts/clear-sans/ClearSans-Medium.ttf",
                        "/myworld/obsidian/skin/fonts/clear-sans/ClearSans-MediumItalic.ttf",
                        "/myworld/obsidian/skin/fonts/clear-sans/ClearSans-Regular.ttf",
                        "/myworld/obsidian/skin/fonts/clear-sans/ClearSans-Thin.ttf"
                ))
                .build();
    }

    public static ComponentSkin checkbox(){
        return ComponentSkinBuilder.create(Checkbox.COMPONENT_STYLE_NAME)
                .withParameter(Checkbox.CHECKED_DATA_NAME, VarType.BOOLEAN)
                .withStyle(
                        StyleClass.forLayer("background",
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, constant(Ellipse.circle(Distance.percentage(100))))
                                        .withRule(COLOR, constant(Colors.WHITE))
                                        .withRule(BORDER_COLOR, constant(ColorRGBA.of("#AAAAAA")))
                                        .withRule(BORDER_WIDTH, constant(1))
                                        .withRule(BORDER_JOIN, constant("round"))
                                        .withRule(BORDER_CAP, constant("round"))
                                .build()
                        )
                )
                .withStyle(
                        StyleClass.forLayerState("background", Component.FOCUSED_DATA_NAME,
                                RuleBuilder.create()
                                        .withRule(STYLES, constant(List.of("DefaultFocus")))
                                .build()
                        )
                )
                .withStyle(
                        StyleClass.forLayerState("check", Checkbox.CHECKED_DATA_NAME,
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, constant(Ellipse.circle(Distance.percentage(40))))
                                        .withRule(POSITION, constant(new Move(
                                                Distance.percentage(12.5f),
                                                Distance.percentage(12.5f)
                                        )))
                                        .withRule(COLOR, constant(ColorRGBA.of("#00000099")))
                                .build()
                        )
                )
                .build();
    }

    public static ComponentSkin viewport(){
        return ComponentSkinBuilder.create(Viewport.COMPONENT_STYLE_NAME)
                .withStyle(
                        StyleClass.forLayer("viewport",
                                RuleBuilder.create()
                                        .withRule(BORDER_COLOR, constant(ColorRGBA.of("#AAAAAA")))
                                        .withRule(BORDER_WIDTH, constant(1))
                                .build()
                        )
                )
                .build();
    }

    public static ComponentSkin textField(){
        return ComponentSkinBuilder.create("TextField")
                .withStyle(
                        StyleClass.forLayer("background",
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, constant(new Rectangle(
                                                Distance.percentage(100),
                                                Distance.percentage(100)
                                        )))
                                        .withRule(COLOR, constant(Colors.WHITE))
                                .build()
                        )
                )
                .withStyle(
                        StyleClass.forLayerState("background", Component.FOCUSED_DATA_NAME,
                                RuleBuilder.create()
                                        .withRule(BORDER_COLOR, constant(FOCUS_COLOR))
                                        .withRule(BORDER_WIDTH, constant(1.5f))
                                        .withRule(BORDER_JOIN, constant("round"))
                                        .withRule(BORDER_CAP, constant("round"))
                                .build()
                        )
                )
                .build();
    }

    public static ComponentSkin editableTextDisplay(){
        return ComponentSkinBuilder.create("EditableTextDisplay")
                .withParameter(EditableTextDisplay.CURSOR_OFFSET_VAR_NAME, VarType.FLOAT)
                .withParameter(EditableTextDisplay.CURSOR_VISIBLE_VAR_NAME, VarType.BOOLEAN)
                .withParameter(EditableTextDisplay.LINE_HEIGHT_VAR_NAME, VarType.FLOAT)
                .withStyle(
                        StyleClass.forForegroundLayerState("cursor", EditableTextDisplay.CURSOR_VISIBLE_VAR_NAME,
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, of(v -> new Rectangle(
                                                Distance.pixels(1),
                                                Distance.pixels(v.get(EditableTextDisplay.LINE_HEIGHT_VAR_NAME, Float.class)))
                                        ))
                                        .withRule(POSITION, of(v -> new Move(
                                                Distance.pixels(v.get(EditableTextDisplay.CURSOR_OFFSET_VAR_NAME, Float.class)),
                                                Distance.pixels(2)
                                        )))
                                        .withRule(COLOR, constant(Colors.BLACK))
                                        .build()
                        )
                )
                .build();
    }

    public static ComponentSkin textDisplay(){
        return ComponentSkinBuilder.create("TextDisplay")
                .withParameter(TextDisplay.TEXT_DATA_NAME, VarType.STRING)
                .withParameter(TextDisplay.FONT_FAMILY_DATA_NAME, VarType.STRING)
                .withParameter(TextDisplay.FONT_STYLE_DATA_NAME, VarType.STRING)
                .withParameter(TextDisplay.FONT_SIZE_DATA_NAME, VarType.FLOAT)
                .withParameter(TextDisplay.TEXT_COLOR_DATA_NAME, VarType.COLOR)
                .withParameter(TextDisplay.SHOW_HIGHLIGHT_DATA_NAME, VarType.BOOLEAN)
                .withParameter(TextDisplay.HIGHLIGHT_COLOR_DATA_NAME, VarType.COLOR)
                .withParameter(TextDisplay.HIGHLIGHT_HEIGHT_DATA_NAME, VarType.FLOAT)
                .withParameter(TextDisplay.HIGHLIGHT_POS_DATA_NAME, VarType.FLOAT)
                .withParameter(TextDisplay.HIGHLIGHT_WIDTH_DATA_NAME, VarType.FLOAT)
                .withStyle(
                        StyleClass.forLayerState("highlight", TextDisplay.SHOW_HIGHLIGHT_DATA_NAME,
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, of(v -> new Rectangle(
                                                Distance.pixels(v.get(TextDisplay.HIGHLIGHT_WIDTH_DATA_NAME, Float.class)),
                                                Distance.pixels(v.get(TextDisplay.HIGHLIGHT_HEIGHT_DATA_NAME, Float.class))))
                                        )
                                        .withRule(POSITION, of(v -> new Move(
                                                Distance.pixels(v.get(TextDisplay.HIGHLIGHT_POS_DATA_NAME, Float.class)),
                                                Distance.pixels(0)))
                                        )
                                        .withRule(COLOR, of(v -> v.get(TextDisplay.HIGHLIGHT_COLOR_DATA_NAME, HIGHLIGHT_COLOR)))
                                        .build()
                        )
                )
                .withStyle(
                        StyleClass.forLayer("text",
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, constant(TextDisplay.TEXT_DATA_NAME))
                                        .withRule(COLOR, of(v -> v.get(TextDisplay.TEXT_COLOR_DATA_NAME, Colors.BLACK)))
                                        .withRule(FONT_FAMILY, of(v -> v.get(TextDisplay.FONT_FAMILY_DATA_NAME, "Clear Sans")))
                                        .withRule(FONT_STYLE, of(v -> v.get(TextDisplay.FONT_STYLE_DATA_NAME, "NORMAL")))
                                        .withRule(FONT_SIZE, of(v -> v.get(TextDisplay.FONT_SIZE_DATA_NAME, 14)))
                                        .build()
                        )
                )
                .build();
    }

    public static ComponentSkin button(){
        return ComponentSkinBuilder.create("Button")
                .withParameter(Button.PRESSED_DATA_NAME, VarType.BOOLEAN)
                .withStyle(
                        StyleClass.forLayer("background",
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, constant(new Rectangle(Distance.percentage(100), Distance.percentage(100))))
                                        .withRule(COLOR, constant(Colors.WHITE))
                                        .withRule(BORDER_COLOR, constant(ColorRGBA.of("#AAAAAA")))
                                        .withRule(BORDER_WIDTH, constant(1))
                                        .withRule(BORDER_JOIN, constant("round"))
                                        .withRule(BORDER_CAP, constant("round"))
                                .build()
                        )
                )
                .withStyle(
                        StyleClass.forLayerState("background", Component.FOCUSED_DATA_NAME,
                                RuleBuilder.create()
                                        .withRule(STYLES, constant(List.of("DefaultFocus")))
                                        .build()
                        )
                )
                .withStyle(
                        StyleClass.forLayerState("background", Button.PRESSED_DATA_NAME,
                                RuleBuilder.create()
                                        .withRule(COLOR, constant(ColorRGBA.of("#AAAAAA")))
                                        .build()
                        )
                )
                .build();
    }

    public static ComponentSkin root(){
        return ComponentSkinBuilder.create("Root")
                .withStyle(
                        StyleClass.forLayer("background",
                                RuleBuilder.create()
                                .withRule(COLOR, constant(Colors.TRANSPARENT))
                                .build()
                        )
                )
                .build();
    }

    public static ComponentSkin example(){
        return ComponentSkinBuilder.create("Example")
                .withParameter("text", VarType.STRING)
                .withStyle(
                        StyleClass.forLayer("background",
                                RuleBuilder.create()
                                        .withRule(GEOMETRY, constant(new Rectangle(Distance.percentage(100), Distance.percentage(100))))
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
