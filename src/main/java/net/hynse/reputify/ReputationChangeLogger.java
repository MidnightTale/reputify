package net.hynse.reputify;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReputationChangeLogger {

    public static void logReputationChange(Player player, int newPoints, ReputationScenario scenario) {
        LocalDateTime currentTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        Bukkit.getServer().getLogger().info("Player " + player.getName() + " reputation changed to " + newPoints +
                " at " + currentTime.format(formatter) + " in scenario " + scenario);
    }
}
