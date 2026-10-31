package me.wesley1808.teleportapi.api.effects;

import eu.pb4.placeholders.api.ServerPlaceholderContext;
import eu.pb4.placeholders.api.parsers.NodeParser;
import me.wesley1808.teleportapi.internal.Formatter;
import me.wesley1808.teleportapi.internal.config.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class TeleportEffectHandler {
    public static final TeleportEffectHandler DEFAULT = new TeleportEffectHandler();

    protected void onWarmupCancelled(ServerPlayer player, String reason, TeleportEffects effects) {
        String format = Config.instance().messages.teleportCancelled;
        if (format == null || format.isEmpty()) {
            return;
        }

        player.sendSystemMessage(Formatter.parse(
                format, Formatter.singlePlaceholderParser("reason", Component.literal(reason)),
                ServerPlaceholderContext.of(player).asParserContext()
        ), Config.instance().showMessagesOnOverlay);
    }

    protected void onWarmupTick(ServerPlayer player, int ticksLeft, TeleportEffects effects) {
        if (ticksLeft % 20 != 0) {
            return;
        }

        int seconds = ticksLeft / 20;
        NodeParser parser = Formatter.placeholderParser((key) -> switch (key) {
            case "label" -> effects.label();
            case "seconds" -> Component.literal(String.format("%d second%s", seconds, seconds == 1 ? "" : "s"));
            default -> null;
        });

        Config.Message config = Config.instance().messages;
        String format = effects.label() != null ? config.teleportCountdownWithLabel : config.teleportCountdown;
        if (format == null || format.isEmpty()) {
            return;
        }

        player.sendSystemMessage(Formatter.parse(
                format, parser,
                ServerPlaceholderContext.of(player).asParserContext()
        ), Config.instance().showMessagesOnOverlay);
    }

    protected void onTeleported(ServerPlayer player, TeleportEffects effects) {
        if (effects.silentTeleportMessage()) {
            return;
        }

        Config.Message config = Config.instance().messages;
        String format = effects.label() != null ? config.teleportSuccessWithLabel : config.teleportSuccess;
        if (format == null || format.isEmpty()) {
            return;
        }

        player.sendSystemMessage(Formatter.parse(
                format, Formatter.singlePlaceholderParser("label", effects.label()),
                ServerPlaceholderContext.of(player).asParserContext()
        ), Config.instance().showMessagesOnOverlay);
    }
}
