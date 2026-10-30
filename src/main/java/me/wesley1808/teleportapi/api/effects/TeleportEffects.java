package me.wesley1808.teleportapi.api.effects;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

public final class TeleportEffects {
    private TeleportEffectHandler handler = TeleportEffectHandler.DEFAULT;
    private boolean silent;
    @Nullable
    private Component label;

    public void onWarmupTick(ServerPlayer player, int ticksLeft) {
        this.handler.onWarmupTick(player, ticksLeft, this);
    }

    public void onWarmupCancelled(ServerPlayer player, String reason) {
        this.handler.onWarmupCancelled(player, reason, this);
    }

    public void onTeleported(ServerPlayer player) {
        this.handler.onTeleported(player, this);
    }

    public boolean silent() {
        return this.silent;
    }

    @Nullable
    public Component label() {
        return this.label;
    }

    public void setHandler(TeleportEffectHandler handler) {
        this.handler = handler;
    }

    public void setSilent(boolean silent) {
        this.silent = silent;
    }

    public void setLabel(@Nullable Component label) {
        this.label = label;
    }
}
