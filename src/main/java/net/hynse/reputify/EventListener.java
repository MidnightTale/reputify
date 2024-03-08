package net.hynse.reputify;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class EventListener implements Listener {

    private final MongoDBManager mongoDBManager;
    private final ReputationManager reputationManager;

    public EventListener(MongoDBManager mongoDBManager, ReputationManager reputationManager) {
        this.mongoDBManager = mongoDBManager;
        this.reputationManager = reputationManager;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        // Load reputation from cache or MongoDB on player join
        if (reputationManager.getReputation(playerId) != 0) {
            reputationManager.setReputation(playerId, mongoDBManager.getPlayerReputation(playerId).getInteger("reputation_points", 0));
        } else {
            mongoDBManager.insertPlayerReputation(playerId);
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = event.getEntity().getKiller();

        if (killer != null) {
            int victimPoints = reputationManager.getReputation(victim.getUniqueId());
            int killerPoints = reputationManager.getReputation(killer.getUniqueId());

            if (victimPoints == 0 && killerPoints >= 0) {
                reputationManager.decreaseReputation(killer.getUniqueId(), 1);
            } else if (victimPoints < 0 && killerPoints >= 0) {
                reputationManager.increaseReputation(killer.getUniqueId(), 1);
            }
        }
    }
}
