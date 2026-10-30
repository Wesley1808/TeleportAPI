package me.wesley1808.teleportapi.api;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public final class TeleportManager {
    private static final List<TeleportWarmupTask> WARMUPS = new ObjectArrayList<>();

    public static void scheduleTeleport(TeleportWarmupTask task) {
        WARMUPS.add(task);
    }

    public static void tickWarmupTasks() {
        if (!WARMUPS.isEmpty()) {
            WARMUPS.removeIf(TeleportWarmupTask::tickOrStop);
        }
    }

    public static boolean isTeleporting(ServerPlayer player) {
        for (TeleportWarmupTask task : WARMUPS) {
            if (task.player == player) {
                return true;
            }
        }
        return false;
    }

    public static TeleportBuilder builder(ServerPlayer player) {
        return new TeleportBuilder(player);
    }
}
