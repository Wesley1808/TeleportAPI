package me.wesley1808.teleportapi.api.effects;

import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import me.wesley1808.teleportapi.internal.Formatter;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.server.level.ServerPlayer;

public class TeleportEffectHandler {
    public static final TeleportEffectHandler DEFAULT = new TeleportEffectHandler();

    protected void onWarmupCancelled(ServerPlayer player, String reason, TeleportEffects effects) {
        player.sendOverlayMessage(Component.literal("Teleport cancelled.. %s.".formatted(reason)).withColor(TextColor.RED));
    }

    protected void onWarmupTick(ServerPlayer player, int ticksLeft, TeleportEffects effects) {
        if (effects.silent() || ticksLeft % 20 != 0) {
            return;
        }

        int seconds = ticksLeft / 20;
        NodeParser parser = Formatter.placeholderParser((key) -> switch (key) {
            case "label" -> effects.label();
            case "seconds" -> Component.literal(String.format("%d second%s", seconds, seconds == 1 ? "" : "s"));
            default -> null;
        });

        player.sendOverlayMessage(Formatter.parse(
                this.getCountdownMessageTemplate(effects.label() != null), parser,
                ServerPlaceholderContext.of(player).asParserContext()
        ));
    }

    protected void onTeleported(ServerPlayer player, TeleportEffects effects) {
        if (effects.silent()) {
            return;
        }

        player.sendOverlayMessage(Formatter.parse(
                this.getPostTeleportMessageTemplate(effects.label() != null),
                Formatter.placeholderParser((key) -> key.equals("label") ? effects.label() : null),
                ServerPlaceholderContext.of(player).asParserContext()
        ));
    }

    protected String getCountdownMessageTemplate(boolean hasLabel) {
        return hasLabel
                ? "<yellow>Teleporting to ${label} in ${seconds}..."
                : "<yellow>Teleporting in ${seconds}...";
    }

    protected String getPostTeleportMessageTemplate(boolean hasLabel) {
        return hasLabel
                ? "<green>Teleported to ${label}"
                : "<green>Teleported";
    }
}
