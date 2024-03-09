package net.hynse.reputify;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.bukkit.ChatColor;

public class ReputifyPlaceholderExpansion extends PlaceholderExpansion {

    private final ReputationManager reputationManager;

    // Constructor to initialize the ReputationManager
    public ReputifyPlaceholderExpansion(ReputationManager reputationManager) {
        this.reputationManager = reputationManager;
    }

    @Override
    public boolean canRegister() {
        // Ensure that PlaceholderAPI is available
        return super.canRegister();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "reputify";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MidnightTale";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String identifier) {
        if (player == null) {
            return "";
        }

        // Handle %reputify_player%
        if (identifier.equals("player")) {
            return String.valueOf(reputationManager.getPlayerReputation(player.getUniqueId()).getInteger("reputation_points"));
        }

        // Handle %reputify_player_colored%
        if (identifier.equals("player_colored")) {
            int reputationPoints = reputationManager.getPlayerReputation(player.getUniqueId()).getInteger("reputation_points");
            ChatColor color;

            if (reputationPoints > 0) {
                color = ChatColor.GREEN; // Positive reputation
            } else if (reputationPoints < 0) {
                color = ChatColor.RED; // Negative reputation
            } else {
                color = ChatColor.YELLOW; // Neutral reputation
            }

            return color + String.valueOf(reputationPoints);
        }

        return null;
    }
}
