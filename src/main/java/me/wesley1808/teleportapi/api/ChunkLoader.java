package me.wesley1808.teleportapi.api;

import me.wesley1808.teleportapi.internal.ChunkTickets;
import net.minecraft.server.level.*;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChunkLoader {
    private static final ChunkResult<List<ChunkAccess>> UNLOADED_CHUNK_HOLDER_RESULT = ChunkResult.error("Chunk holder at ticket position is still unloaded");

    public static Ticket createTicket(int radius) {
        return new Ticket(ChunkTickets.LOAD_CHUNK, ChunkLevel.byStatus(FullChunkStatus.FULL) - radius);
    }

    public static CompletableFuture<ChunkResult<List<ChunkAccess>>> addTicketAndLoadWithRadius(ServerChunkCache chunkCache, ChunkPos pos, int radius) {
        return addTicketAndLoadWithRadius(chunkCache, createTicket(radius), pos, radius);
    }

    public static CompletableFuture<ChunkResult<List<ChunkAccess>>> addTicketAndLoadWithRadius(ServerChunkCache chunkCache, Ticket ticket, ChunkPos pos, int radius) {
        chunkCache.addTicket(ticket, pos);
        chunkCache.runDistanceManagerUpdates();

        ChunkHolder holder = chunkCache.getVisibleChunkIfPresent(pos.pack());
        if (holder == null) {
            return CompletableFuture.completedFuture(UNLOADED_CHUNK_HOLDER_RESULT);
        }

        return chunkCache.chunkMap.getChunkRangeFuture(holder, radius, _ -> ChunkStatus.FULL);
    }
}
