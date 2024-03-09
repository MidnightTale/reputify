package net.hynse.reputify;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EventListener implements Listener {

    // Map to store recent kills
    // Map to store recent kills
    private final Map<UUID, Map<UUID, Long>> recentKills = new HashMap<>();
    private final MongoDBManager mongoDBManager;

    // Constructor to initialize the MongoDBManager
    public EventListener(MongoDBManager mongoDBManager) {
        this.mongoDBManager = mongoDBManager;
    }


    // Define a cooldown period in milliseconds (3 minutes)
    private static final long COOLDOWN_PERIOD = 3 * 60 * 1000;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Load reputation from HashMap on player join
        recentKills.put(playerId, new HashMap<>());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            UUID victimId = victim.getUniqueId();
            UUID killerId = killer.getUniqueId();

            // Check if killer's recent kills map exists, if not create it
            recentKills.computeIfAbsent(killerId, k -> new HashMap<>());

            // Check if the victim is in the killer's recent kills map
            if (hasRecentKill(killerId, victimId, COOLDOWN_PERIOD)) {
                long remainingCooldown = calculateRemainingCooldown(killerId, victimId, COOLDOWN_PERIOD);
                killer.sendMessage("Remaining cooldown time: " + remainingCooldown + " milliseconds");
                return;
            }

            addRecentKill(killerId, victimId);

            int victimPoints = mongoDBManager.getPlayerReputation(victimId).getInteger("reputation_points");
            int killerPoints = mongoDBManager.getPlayerReputation(killerId).getInteger("reputation_points");

            int newKillerPoints;

            switch (getReputationScenario(victimPoints, killerPoints)) {
                case SCENARIO_1:
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_4:
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_5:
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_7:
                    newKillerPoints = killerPoints + 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_3:
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_6:
                    newKillerPoints = killerPoints + 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_2:
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;
                case SCENARIO_8:
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;
                case SCENARIO_9:
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;
                case NO_CHANGE:
                    newKillerPoints = killerPoints;
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;
            }

        }

    }



    // Helper method to determine the reputation scenario
    private ReputationScenario getReputationScenario(int victimPoints, int killerPoints) {
        if (victimPoints > 0 && killerPoints < 0) { // killerPoints -1
            return ReputationScenario.SCENARIO_1;
        } else if (victimPoints > 0 && killerPoints > 0) { // killerPoints -1
            return ReputationScenario.SCENARIO_4;
        } else if (victimPoints > 0 && killerPoints == 0) { // killerPoints -1
            return ReputationScenario.SCENARIO_5;
        } else if (victimPoints < 0 && killerPoints < 0) { // no change
            return ReputationScenario.NO_CHANGE;
        } else if (victimPoints < 0 && killerPoints > 0) { // killerPoints +1
            return ReputationScenario.SCENARIO_6;
        } else if (victimPoints < 0 && killerPoints == 0) { // killerPoints +1
            return ReputationScenario.SCENARIO_7;
        } else if (victimPoints == 0 && killerPoints == 0) { // killerPoints -1
            return ReputationScenario.SCENARIO_2;
        } else if (victimPoints == 0 && killerPoints > 0) { // killerPoints -1
            return ReputationScenario.SCENARIO_8;
        } else if (victimPoints == 0 && killerPoints < 0) { // killerPoints -1
            return ReputationScenario.SCENARIO_9;
        } else if (victimPoints > 0 && killerPoints < 0) { // killerPoints +1
            return ReputationScenario.SCENARIO_3;
        } else {
            // No change for other scenarios
            return ReputationScenario.NO_CHANGE;
        }
    }




    // Enum to represent different reputation scenarios
    private enum ReputationScenario {
        SCENARIO_1,
        SCENARIO_2,
        SCENARIO_3,
        SCENARIO_4,
        SCENARIO_5,
        SCENARIO_6,
        SCENARIO_7,
        SCENARIO_8,
        SCENARIO_9,
        NO_CHANGE
    }



    private void logReputationChange(Player player, int newPoints, ReputationScenario scenario) {
        // Get current timestamp
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Log reputation change in game chat
        Bukkit.getServer().getLogger().info("Player " + player.getName() + " reputation changed to " + newPoints +
                " at " + currentTime.format(formatter) + " in scenario " + scenario);
    }
    // Check if a recent kill exists
    private boolean hasRecentKill(UUID playerId, UUID victimId, long cooldownPeriod) {
        if (recentKills.containsKey(playerId) && recentKills.get(playerId).containsKey(victimId)) {
            long lastKillTime = recentKills.get(playerId).get(victimId);
            long currentTime = System.currentTimeMillis();
            return currentTime - lastKillTime < cooldownPeriod;
        }
        return false;
    }

    // Add a recent kill
    private void addRecentKill(UUID playerId, UUID victimId) {
        recentKills.computeIfAbsent(playerId, k -> new HashMap<>());
        recentKills.get(playerId).put(victimId, System.currentTimeMillis());
    }

    // Helper method to calculate remaining cooldown
    private long calculateRemainingCooldown(UUID killerId, UUID victimId, long cooldownPeriod) {
        long lastKillTime = getRecentKillTime(killerId, victimId);
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
    private long getRecentKillTime(UUID playerId, UUID victimId) {
        if (recentKills.containsKey(playerId) && recentKills.get(playerId).containsKey(victimId)) {
            return recentKills.get(playerId).get(victimId);
        }
        return 0; // Return 0 if no recent kill time is found
    }
}
