package me.wesley1808.teleportapi.api;

import org.jetbrains.annotations.Nullable;

public interface TeleportCancelCheck {
    TeleportCancelCheck NONE = (_) -> null;

    TeleportCancelCheck HURT = (task) -> {
        return task.player.hurtTime > 0 ? "you took damage" : null;
    };

    TeleportCancelCheck MOVE = (task) -> {
        return task.player.level() != task.originalLevel || !task.player.position().closerThan(task.originalPos, 2)
                ? "you moved"
                : null;
    };

    TeleportCancelCheck DEFAULT = TeleportCancelCheck.combine(HURT, MOVE);

    @Nullable String validate(TeleportWarmupTask task);

    static TeleportCancelCheck combine(TeleportCancelCheck... checks) {
        return (task) -> {
            for (TeleportCancelCheck check : checks) {
                String result = check.validate(task);
                if (result != null) {
                    return result;
                }
            }
            return null;
        };
    }
}
