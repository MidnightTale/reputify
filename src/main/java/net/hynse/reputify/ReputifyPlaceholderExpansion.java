package net.hynse.reputify;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

        // Handle %reputify_player_prefix%
        if (identifier.equals("player_prefix")) {
            return getPrefix();
        }

        // Handle %reputify_player%
        if (identifier.equals("player")) {
            return player.getName();
        }

        // Handle %reputify_player_{name}%
        if (identifier.startsWith("player_")) {
            String attribute = identifier.substring("player_".length());
            return getPlayerAttribute(player, attribute);
        }

        return null;
    }

    private String getPrefix() {
        return "Prefix";
    }

    private String getPlayerAttribute(Player player, String attribute) {
        return switch (attribute) {
            case "name" -> player.getName();
            case "uuid" -> player.getUniqueId().toString();
            case "reputation" ->
                    String.valueOf(reputationManager.getPlayerReputation(player.getUniqueId()).getInteger("reputation_points"));
            case "prefix" -> reputationManager.getPlayerPrefix(player.getUniqueId());
            default -> "";
        };
    }
}
