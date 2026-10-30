package me.wesley1808.teleportapi.internal;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.TicketType;

public final class ChunkTickets {
    public static final TicketType LOAD_CHUNK = registerTicketType(
            TeleportAPI.id("load_chunk"),
            20L, TicketType.FLAG_LOADING
    );

    public static void register() {
    }

    private static TicketType registerTicketType(Identifier id, long timeOut, int flag) {
        return Registry.register(BuiltInRegistries.TICKET_TYPE, id, new TicketType(timeOut, flag));
    }
}