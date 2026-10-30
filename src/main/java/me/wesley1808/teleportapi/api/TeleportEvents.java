package me.wesley1808.teleportapi.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

public interface TeleportEvents {

    Event<BeginWarmup> BEGIN_WARMUP = EventFactory.createArrayBacked(BeginWarmup.class, (callbacks) -> (task) -> {
        for (BeginWarmup callback : callbacks) {
            callback.onWarmupStart(task);
        }
    });

    Event<CancelWarmup> CANCEL_WARMUP = EventFactory.createArrayBacked(CancelWarmup.class, (callbacks) -> (task) -> {
        for (CancelWarmup callback : callbacks) {
            callback.onWarmupCancel(task);
        }
    });

    Event<PostTeleport> POST_TELEPORT = EventFactory.createArrayBacked(PostTeleport.class, (callbacks) -> (player, fromLevel, fromPos, fromYRot, fromXRot) -> {
        for (PostTeleport callback : callbacks) {
            callback.onPostTeleport(player, fromLevel, fromPos, fromYRot, fromXRot);
        }
    });

    @FunctionalInterface
    interface BeginWarmup {
        void onWarmupStart(TeleportWarmupTask task);
    }

    @FunctionalInterface
    interface CancelWarmup {
        void onWarmupCancel(ServerPlayer player);
    }

    @FunctionalInterface
    interface PostTeleport {
        void onPostTeleport(ServerPlayer player, ServerLevel fromLevel, Vec3 fromPos, float fromYRot, float fromXRot);
    }
}
