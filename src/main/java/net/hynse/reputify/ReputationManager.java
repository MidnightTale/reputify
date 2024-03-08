/**
 * ReputationManager Class
 * Manages in-memory player reputation cache.
 */
package net.hynse.reputify;

import java.util.Map;
import java.util.UUID;

public class ReputationManager {

    private final Map<UUID, Integer> reputationCache;

    public ReputationManager(Map<UUID, Integer> reputationCache) {
        this.reputationCache = reputationCache;
    }

    public int getReputation(UUID playerId) {
        // Get player reputation from cache or default to 0
        return reputationCache.getOrDefault(playerId, 0);
    }

    public void setReputation(UUID playerId, int points) {
        // Set player reputation in the cache
        reputationCache.put(playerId, points);
    }

    public void increaseReputation(UUID playerId, int points) {
        // Increase player reputation in the cache
        setReputation(playerId, getReputation(playerId) + points);
    }

    public void decreaseReputation(UUID playerId, int points) {
        // Decrease player reputation in the cache
        setReputation(playerId, getReputation(playerId) - points);
    }
}
