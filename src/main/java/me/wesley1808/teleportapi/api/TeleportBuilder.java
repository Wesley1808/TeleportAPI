package me.wesley1808.teleportapi.api;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.wesley1808.teleportapi.api.effects.TeleportEffectHandler;
import me.wesley1808.teleportapi.api.effects.TeleportEffects;
import me.wesley1808.teleportapi.internal.TeleportAPI;
import net.minecraft.core.SectionPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.Ticket;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Relative;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

public class TeleportBuilder {
    private static final int CHUNK_LOAD_RADIUS = 1;

    public final ServerPlayer player;
    private final TeleportEffects effects = new TeleportEffects();
    private ServerLevel level;
    private Vec3 pos;
    private float yRot = 0;
    private float xRot = 0;
    private int warmupTicks = 60;
    private boolean sync = false;
    private TeleportCancelCheck cancelCheck = TeleportCancelCheck.DEFAULT;

    private Set<Relative> relatives = Set.of();

    @Nullable
    private Predicate<ServerLevel> preTeleport;
    @Nullable
    private BooleanConsumer onFinished;

    protected TeleportBuilder(ServerPlayer player) {
        this.player = player;
    }

    public TeleportBuilder to(ServerLevel level, Vec3 pos) {
        this.level = level;
        this.pos = pos;
        return this;
    }

    public TeleportBuilder rotation(Vec2 rot) {
        return this.rotation(rot.y, rot.x);
    }

    public TeleportBuilder rotation(float yRot, float xRot) {
        this.yRot = yRot;
        this.xRot = xRot;
        return this;
    }

    public TeleportBuilder relatives(Set<Relative> relatives) {
        this.relatives = relatives;
        return this;
    }

    public TeleportBuilder sync() {
        this.sync = true;
        return this.instant();
    }

    public TeleportBuilder instant() {
        return this.warmup(0);
    }

    public TeleportBuilder warmup(int warmupTicks) {
        this.warmupTicks = warmupTicks;
        return this;
    }

    public TeleportBuilder effects(TeleportEffectHandler handler) {
        this.effects.setHandler(handler);
        return this;
    }

    public TeleportBuilder label(String label) {
        return this.label(Component.literal(label));
    }

    public TeleportBuilder label(Component label) {
        this.effects.setLabel(label);
        return this;
    }

    public TeleportBuilder silent(boolean silent) {
        this.effects.setSilent(silent);
        return this;
    }

    public TeleportBuilder cancelCheck(TeleportCancelCheck cancelCheck) {
        this.cancelCheck = cancelCheck;
        return this;
    }

    public TeleportBuilder preTeleport(@Nullable Predicate<ServerLevel> preTeleport) {
        this.preTeleport = preTeleport;
        return this;
    }

    public TeleportBuilder onFinished(BooleanConsumer onFinished) {
        this.onFinished = onFinished;
        return this;
    }

    public void execute() {
        if (this.level == null || this.pos == null) {
            TeleportAPI.LOGGER.error("Ignoring teleport: destination has not been set!");
            return;
        }

        // Execute teleport immediately if the sync flag is set to true.
        // Guarantees that the player will have been moved on the next call.
        if (this.sync) {
            this.executeTeleport();
            return;
        }

        // If a sync teleport is not required, wait for the chunks to load async before actually teleporting the player.
        // This prevents lag spikes caused by players ticking in unloaded chunks.
        ChunkPos chunkPos = new ChunkPos(
                SectionPos.blockToSectionCoord(Mth.floor(this.pos.x)),
                SectionPos.blockToSectionCoord(Mth.floor(this.pos.z))
        );

        Ticket ticket = ChunkLoader.createTicket(CHUNK_LOAD_RADIUS);
        CompletableFuture<?> chunkLoadFuture = ChunkLoader.addTicketAndLoadWithRadius(this.level.getChunkSource(), ticket, chunkPos, CHUNK_LOAD_RADIUS);
        if (this.warmupTicks > 0) {
            TeleportManager.scheduleTeleport(new TeleportWarmupTask(
                    this.player, this.warmupTicks, ticket,
                    this.cancelCheck, this.onFinished, this.effects,
                    () -> chunkLoadFuture.thenAcceptAsync(_ -> this.executeTeleport(), this.level.getServer())
            ));
        } else {
            chunkLoadFuture.thenAcceptAsync(_ -> this.executeTeleport(), this.level.getServer());
        }
    }

    private void executeTeleport() {
        if (this.preTeleport == null || this.preTeleport.test(level)) {
            final Vec3 fromPos = this.player.position();
            final ServerLevel fromLevel = this.player.level();
            final float fromYRot = this.player.getYRot();
            final float fromXRot = this.player.getXRot();

            this.player.teleportTo(this.level, this.pos.x, this.pos.y, this.pos.z, this.relatives, this.yRot, this.xRot, true);
            this.effects.onTeleported(this.player);

            TeleportEvents.POST_TELEPORT.invoker().onPostTeleport(this.player, fromLevel, fromPos, fromYRot, fromXRot);
            if (this.onFinished != null) {
                this.onFinished.accept(true);
            }
        } else {
            TeleportEvents.CANCEL_WARMUP.invoker().onWarmupCancel(this.player);
            if (this.onFinished != null) {
                this.onFinished.accept(false);
            }
        }
    }
}
