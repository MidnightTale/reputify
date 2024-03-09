package net.hynse.reputify;

import java.util.Map;
import java.util.UUID;
import java.util.HashMap;
public class ReputationUtils {
    // Helper method to determine the reputation scenario
    public static ReputationScenario getReputationScenario(int victimPoints, int killerPoints) {
        if (victimPoints > 0 && killerPoints < 0) { // killerPoints -3
            return ReputationScenario.SCENARIO_1;
        } else if (victimPoints > 0 && killerPoints > 0) { // killerPoints -1
            return ReputationScenario.SCENARIO_4;
        } else if (victimPoints > 0 && killerPoints == 0) { // killerPoints -2
            return ReputationScenario.SCENARIO_5;
        } else if (victimPoints < 0 && killerPoints < 0) { // no change
            return ReputationScenario.NO_CHANGE;
        } else if (victimPoints < 0 && killerPoints > 0) { // killerPoints +3
            return ReputationScenario.SCENARIO_6;
        } else if (victimPoints < 0 && killerPoints == 0) { // killerPoints +2
            return ReputationScenario.SCENARIO_7;
        } else if (victimPoints == 0 && killerPoints == 0) { // killerPoints -1
            return ReputationScenario.SCENARIO_2;
        } else if (victimPoints == 0 && killerPoints > 0) { // killerPoints -1
            return ReputationScenario.SCENARIO_8;
        } else if (victimPoints == 0 && killerPoints < 0) { // killerPoints -1
            return ReputationScenario.SCENARIO_9;
        } else if (victimPoints > 0 && killerPoints < 0) { // killerPoints -2
            return ReputationScenario.SCENARIO_3;
        } else {
            // No change for other scenarios
            return ReputationScenario.NO_CHANGE;
        }
    }

    // Check if a recent kill exists
    public static boolean hasRecentKill(UUID playerId, UUID victimId, long cooldownPeriod, Map<UUID, Map<UUID, Long>> recentKills) {
        if (recentKills.containsKey(playerId) && recentKills.get(playerId).containsKey(victimId)) {
            long lastKillTime = recentKills.get(playerId).get(victimId);
            long currentTime = System.currentTimeMillis();
            return currentTime - lastKillTime < cooldownPeriod;
        }
        return false;
    }

    // Add a recent kill
    public static void addRecentKill(UUID playerId, UUID victimId, Map<UUID, Map<UUID, Long>> recentKills) {
        recentKills.computeIfAbsent(playerId, k -> new HashMap<>());
        recentKills.get(playerId).put(victimId, System.currentTimeMillis());
    }

    // Helper method to calculate remaining cooldown
    public static long calculateRemainingCooldown(long cooldownPeriod, long lastKillTime) {
        long currentTime = System.currentTimeMillis();

        if (lastKillTime > 0) {
            // Calculate remaining cooldown only if there's a recent kill
            long elapsedTime = currentTime - lastKillTime;
            return Math.max(0, cooldownPeriod - elapsedTime);
        } else {
            // No recent kill, return the full cooldown period
            return cooldownPeriod;
        }
    }



    // Get the time of the most recent kill
    public static long getRecentKillTime(UUID playerId, UUID victimId, Map<UUID, Map<UUID, Long>> recentKills) {
        if (recentKills.containsKey(playerId) && recentKills.get(playerId).containsKey(victimId)) {
            return recentKills.get(playerId).get(victimId);
        }
        return 0; // Return 0 if no recent kill time is found
    }
}
