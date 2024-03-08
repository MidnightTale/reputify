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

    private final MongoDBManager mongoDBManager;

    public EventListener(MongoDBManager mongoDBManager) {
        this.mongoDBManager = mongoDBManager;
    }
    // Define a cooldown period in milliseconds (e.g., 3 minutes)
    private static final long COOLDOWN_PERIOD = 3 * 60 * 1000; // 3 minutes in milliseconds

    // Use a hashmap to store recent kills with timestamps
    private Map<UUID, Map<UUID, Long>> recentKills = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Load reputation from MongoDB on player join
        if (mongoDBManager.getPlayerReputation(playerId) == null) {
            mongoDBManager.insertPlayerReputation(playerId);
        }
    }
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            UUID victimId = victim.getUniqueId();
            UUID killerId = killer.getUniqueId();

            // Check if killer's recent kills map exists, if not create it
            recentKills.putIfAbsent(killerId, new HashMap<>());

            // Check if the victim is in the killer's recent kills map
            if (recentKills.get(killerId).containsKey(victimId)) {
                long lastKillTime = recentKills.get(killerId).get(victimId);
                long currentTime = System.currentTimeMillis();

                // Check if the cooldown period has passed since the last kill
                if (currentTime - lastKillTime < COOLDOWN_PERIOD) {
                    // Apply cooldown logic here (e.g., ignore the kill or penalize the killer)
                    return;
                }
            }
            recentKills.get(killerId).put(victimId, System.currentTimeMillis());

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
                    newKillerPoints = killerPoints + 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;
                case SCENARIO_8:
                    newKillerPoints = killerPoints + 1;
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
        } else if (victimPoints == 0 && killerPoints > 0) { // killerPoints +1
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

}
