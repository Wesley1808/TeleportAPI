package me.wesley1808.teleportapi.api.effects;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.wesley1808.teleportapi.internal.config.Config;

import java.util.Map;

public class DefaultTeleportEffects {
    private static final Map<String, TeleportEffectHandler> EFFECTS = new Object2ObjectOpenHashMap<>();

    public static void register(String key, TeleportEffectHandler handler) {
        EFFECTS.put(key, handler);
    }

    public static TeleportEffectHandler getDefault() {
        String key = Config.instance().defaultTeleportEffect;
        return get(key);
    }

    public static TeleportEffectHandler get(String key) {
        return EFFECTS.getOrDefault(key, TeleportEffectHandler.DEFAULT);
    }
}
