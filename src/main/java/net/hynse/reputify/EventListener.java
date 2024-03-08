package net.hynse.reputify;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class EventListener implements Listener {

    private final MongoDBManager mongoDBManager;

    public EventListener(MongoDBManager mongoDBManager) {
        this.mongoDBManager = mongoDBManager;
    }

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

            int victimPoints = mongoDBManager.getPlayerReputation(victimId).getInteger("reputation_points");
            int killerPoints = mongoDBManager.getPlayerReputation(killerId).getInteger("reputation_points");

            int newKillerPoints;
            int newVictimPoints;

            switch (getReputationScenario(victimPoints, killerPoints)) {
                case SCENARIO_1:
                    // X positive rep kill G negative rep / X will get + / G no change
                    newKillerPoints = killerPoints + 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_4:
                    // X positive rep kill G positive rep / X will get - / G rep no change
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_5:
                    // X positive rep kill G 0 rep / X will get - / 0 will get no change
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_7:
                    // X 0 rep kill G positive rep / X will get - / G will get no change
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_3:
                    // X negative rep kill G positive rep / X will get - / G will get no change
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_6:
                    // X negative rep kill G 0 rep / X will get - / G will get no change
                    newKillerPoints = killerPoints - 1;
                    mongoDBManager.updatePlayerReputation(killerId, newKillerPoints);
                    logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                    break;

                case SCENARIO_2:
                case SCENARIO_8:
                case SCENARIO_9:
                    // No change scenarios, do nothing
                    break;
            }
        }
    }



    // Helper method to determine the reputation scenario
    private ReputationScenario getReputationScenario(int victimPoints, int killerPoints) {
        if (victimPoints > 0 && killerPoints < 0) {
            // X positive rep kills G negative rep
            return ReputationScenario.SCENARIO_1;
        } else if (victimPoints > 0 && killerPoints > 0) {
            // X positive rep kills G positive rep
            return ReputationScenario.SCENARIO_4;
        } else if (victimPoints > 0 && killerPoints == 0) {
            // X positive rep kills G 0 rep
            return ReputationScenario.SCENARIO_5;
        } else if (victimPoints < 0 && killerPoints < 0) {
            // X negative rep kills G negative rep
            return ReputationScenario.NO_CHANGE;
        } else if (victimPoints < 0 && killerPoints > 0) {
            // X negative rep kills G positive rep
            return ReputationScenario.SCENARIO_6;
        } else if (victimPoints < 0 && killerPoints == 0) {
            // X negative rep kills G 0 rep
            return ReputationScenario.SCENARIO_7;
        } else if (victimPoints == 0 && killerPoints == 0) {
            // X 0 rep kills G 0 rep
            return ReputationScenario.SCENARIO_2;
        } else if (victimPoints == 0 && killerPoints > 0) {
            // X 0 rep kills G positive rep
            return ReputationScenario.SCENARIO_8;
        } else if (victimPoints == 0 && killerPoints < 0) {
            // X 0 rep kills G negative rep
            return ReputationScenario.SCENARIO_9;
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
