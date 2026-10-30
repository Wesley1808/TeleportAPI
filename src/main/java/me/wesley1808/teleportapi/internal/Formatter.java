package me.wesley1808.teleportapi.internal;

import eu.pb4.placeholders.api.ParserContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import eu.pb4.placeholders.api.parsers.ParserBuilder;
import eu.pb4.placeholders.api.parsers.TagLikeParser;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

import java.util.function.Function;

public class Formatter {
    public static final NodeParser BASE_PARSER = baseParser().build();

    public static Component parse(String text, NodeParser parser, ParserContext context) {
        return parser.parseComponent(text, context);
    }

    public static NodeParser placeholderParser(Function<String, @Nullable Component> placeholderMapper) {
        return baseParser()
                .customTags(TagLikeParser.PLACEHOLDER_USER, TagLikeParser.Provider.placeholderText(placeholderMapper))
                .build();
    }

    private static ParserBuilder baseParser() {
        return NodeParser.builder()
                .simplifiedTextFormat()
                .quickText()
                .requireSafe()
                .serverPlaceholders();
    }
}
