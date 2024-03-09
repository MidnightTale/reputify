package net.hynse.reputify;

import me.nahu.scheduler.wrapper.runnable.WrappedRunnable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.hynse.reputify.ReputationChangeLogger.logReputationChange;
import static net.hynse.reputify.ReputationUtils.*;

public class EventListener implements Listener {

    // Map to store recent kills
    private final Map<UUID, Map<UUID, Long>> recentKills = new HashMap<>();
    private final ReputationManager reputationManager;

    // Constructor to initialize the MongoDBManager
    public EventListener(ReputationManager reputationManager) {
        this.reputationManager = reputationManager;
    }


    // Define a cooldown period in milliseconds (3 minutes)
    private static final long COOLDOWN_PERIOD = 3 * 60 * 1000;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        //reputationManager.updatePlayerDisplayName(event.getPlayer());

        // Load reputation from HashMap on player join
        recentKills.put(playerId, new HashMap<>());
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        //reputationManager.updatePlayerDisplayName(event.getPlayer());
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            UUID victimId = victim.getUniqueId();
            UUID killerId = killer.getUniqueId();

            // Check if killer's recent kills map exists, if not create it
            recentKills.computeIfAbsent(killerId, k -> new HashMap<>());

            // Check if the victim is in the killer's recent kills map
            if (hasRecentKill(killerId, victimId, COOLDOWN_PERIOD, recentKills)) {
                long remainingCooldown = calculateRemainingCooldown(COOLDOWN_PERIOD, getRecentKillTime(killerId, victimId, recentKills));
                killer.sendMessage("Remaining cooldown time: " + remainingCooldown + " milliseconds");
                return;
            }

            addRecentKill(killerId, victimId, recentKills);
            new WrappedRunnable() {
                @Override
                public void run() {
                    int victimPoints = reputationManager.getPlayerReputation(victimId).getInteger("reputation_points");
                    int killerPoints = reputationManager.getPlayerReputation(killerId).getInteger("reputation_points");

                    int newKillerPoints;

                    switch (getReputationScenario(victimPoints, killerPoints)) {
                        case SCENARIO_1:
                            newKillerPoints = killerPoints - 3;
                            reputationManager.updatePlayerReputation(killerId, newKillerPoints);
                            logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                            break;

                        case SCENARIO_4:
                            newKillerPoints = killerPoints - 1;
                            reputationManager.updatePlayerReputation(killerId, newKillerPoints);
                            logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                            break;

                        case SCENARIO_5:
                            newKillerPoints = killerPoints - 2;
                            reputationManager.updatePlayerReputation(killerId, newKillerPoints);
                            logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                            break;

                        case SCENARIO_7:
                            newKillerPoints = killerPoints + 2;
                            reputationManager.updatePlayerReputation(killerId, newKillerPoints);
                            logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                            break;

                        case SCENARIO_3:
                            newKillerPoints = killerPoints - 2;
                            reputationManager.updatePlayerReputation(killerId, newKillerPoints);
                            logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                            break;

                        case SCENARIO_6:
                            newKillerPoints = killerPoints + 3;
                            reputationManager.updatePlayerReputation(killerId, newKillerPoints);
                            logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                            break;

                        case SCENARIO_2:
                            newKillerPoints = killerPoints - 1;
                            reputationManager.updatePlayerReputation(killerId, newKillerPoints);
                            logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                            break;
                        case SCENARIO_8:
                            newKillerPoints = killerPoints - 1;
                            reputationManager.updatePlayerReputation(killerId, newKillerPoints);
                            logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                            break;
                        case SCENARIO_9:
                            newKillerPoints = killerPoints - 1;
                            reputationManager.updatePlayerReputation(killerId, newKillerPoints);
                            logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                            break;
                        case NO_CHANGE:
                            newKillerPoints = killerPoints;
                            logReputationChange(killer, newKillerPoints, getReputationScenario(victimPoints, killerPoints));
                            break;
                    }

                }
            }.runTaskAsynchronously(Reputify.instance);

        }
    }
}
