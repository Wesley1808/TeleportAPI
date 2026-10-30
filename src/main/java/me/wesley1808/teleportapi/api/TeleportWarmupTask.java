package me.wesley1808.teleportapi.api;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.wesley1808.teleportapi.api.effects.TeleportEffects;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class TeleportWarmupTask {
    public final ServerPlayer player;
    public final Vec3 originalPos;
    public final ServerLevel originalLevel;
    private final Ticket chunkLoadTicket;
    private final Runnable callback;
    private final TeleportCancelCheck cancelCheck;
    private final TeleportEffects effects;
    @Nullable
    private final BooleanConsumer onFinished;

    private int ticksRemaining;
    private boolean started;

    public TeleportWarmupTask(
            ServerPlayer player, int ticks,
            Ticket chunkLoadTicket,
            TeleportCancelCheck cancelCheck,
            @Nullable BooleanConsumer onFinished,
            TeleportEffects effects,
            Runnable callback
    ) {
        this.callback = callback;
        this.effects = effects;
        this.onFinished = onFinished;
        this.cancelCheck = cancelCheck;
        this.chunkLoadTicket = chunkLoadTicket;
        this.player = player;
        this.originalPos = player.position();
        this.originalLevel = player.level();
        this.ticksRemaining = ticks + 1;
        this.started = false;
    }

    public int ticksRemaining() {
        return this.ticksRemaining;
    }

    public final boolean tickOrStop() {
        State state = this.tickOrStopInternal();
        if (state != State.CONTINUE) {
            if (state == State.CANCELLED) {
                if (this.started) {
                    TeleportEvents.CANCEL_WARMUP.invoker().onWarmupCancel(this.player);
                }

                if (this.onFinished != null) {
                    this.onFinished.accept(false);
                }
            }
            return true;
        }

        if (!this.started) {
            this.started = true;
            TeleportEvents.BEGIN_WARMUP.invoker().onWarmupStart(this);
        }

        this.chunkLoadTicket.resetTicksLeft();
        return false;
    }

    private State tickOrStopInternal() {
        if (this.player.hasDisconnected() || !this.player.isAlive()) {
            return State.CANCELLED;
        }

        String reason = this.cancelCheck.validate(this);
        if (reason != null) {
            this.effects.onWarmupCancelled(this.player, reason);
            return State.CANCELLED;
        }

        if (--this.ticksRemaining <= 0) {
            this.callback.run();
            return State.FINISHED;
        }

        this.effects.onWarmupTick(this.player, this.ticksRemaining);
        return State.CONTINUE;
    }

    private enum State {
        FINISHED,
        CANCELLED,
        CONTINUE
    }
}
