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
            int victimPoints = mongoDBManager.getPlayerReputation(victim.getUniqueId()).getInteger("reputation_points");
            int killerPoints = mongoDBManager.getPlayerReputation(killer.getUniqueId()).getInteger("reputation_points");

            if (victimPoints == 0) {
                if (killerPoints >= 0) {
                    mongoDBManager.updatePlayerReputation(killer.getUniqueId(), killerPoints - 1);
                    logReputationChange(killer, killerPoints - 1);
                } else {
                    mongoDBManager.updatePlayerReputation(killer.getUniqueId(), killerPoints + 1);
                    logReputationChange(killer, killerPoints + 1);
                }
            } else if (victimPoints < 0 && killerPoints >= 0) {
                mongoDBManager.updatePlayerReputation(killer.getUniqueId(), killerPoints + 1);
                logReputationChange(killer, killerPoints + 1);
            }
        }
    }

    private void logReputationChange(Player player, int newPoints) {
        // Get current timestamp
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Log reputation change in game chat
        Bukkit.getServer().getLogger().info("Player " + player.getName() + " reputation changed to " + newPoints + " at " + currentTime.format(formatter));
    }
}
