package me.wesley1808.teleportapi.internal;

import com.mojang.logging.LogUtils;
import me.wesley1808.teleportapi.api.TeleportManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

public class TeleportAPI implements ModInitializer {
    public static final String MOD_ID = "teleportapi";
    public static final Logger LOGGER = LogUtils.getLogger();

    private static final Identifier PRE_PHASE = id("pre");

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        ChunkTickets.register();

        ServerTickEvents.END_SERVER_TICK.addPhaseOrdering(PRE_PHASE, Event.DEFAULT_PHASE);
        ServerTickEvents.END_SERVER_TICK.register(PRE_PHASE, (server) -> {
            TeleportManager.tickWarmupTasks();
        });
    }
}
