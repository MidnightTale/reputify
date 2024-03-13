package net.hynse.reputify;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ReputationCommands implements CommandExecutor, TabCompleter {

    private final MongoDBManager mongoDBManager;

    public ReputationCommands(MongoDBManager mongoDBManager) {
        this.mongoDBManager = mongoDBManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use these commands.");
            return true;
        }

        if (args.length == 0&& sender.hasPermission("reputify.view")) {
            // If no arguments provided, show sender's reputation
            int reputationPoints = mongoDBManager.getPlayerReputation(player.getUniqueId()).getInteger("reputation_points");
            player.sendMessage("Your reputation points: " + reputationPoints);
            return true;
        }

        if (args.length == 1 && sender.hasPermission("reputify.view.other")) {
            // Check if the sender has permission to view other players' reputation
            try {
                String targetPlayerName = args[0];
                Player targetPlayer = player.getServer().getPlayer(targetPlayerName);

                if (targetPlayer == null) {
                    player.sendMessage("Player not found.");
                    return true;
                }

                // Get target player's reputation
                int reputationPoints = mongoDBManager.getPlayerReputation(targetPlayer.getUniqueId()).getInteger("reputation_points");
                player.sendMessage(targetPlayer.getName() + "'s reputation points: " + reputationPoints);
                return true;
            } catch (NullPointerException e) {
                player.sendMessage("Player not found or has no reputation points.");
                return true;
            } catch (Exception e) {
                player.sendMessage("An error occurred while retrieving the player's reputation points.");
                e.printStackTrace(); // Print stack trace for debugging
                return true;
            }
        }


        // If the command is not /rep {player}, check for admin commands
        if (args.length != 3) {
            player.sendMessage("Usage: /rep [player] [points]");
            return true;
        }

        String subCommand = args[0];
        String targetPlayerName = args[1];
        Player targetPlayer = player.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            player.sendMessage("Player not found.");
            return true;
        }

        int points;
        try {
            points = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            player.sendMessage("Invalid number format for points.");
            return true;
        }

        switch (subCommand.toLowerCase()) {
            case "add":
                if (!sender.hasPermission("reputify.manage.add")) {
                    sender.sendMessage("You don't have permission to add reputation points.");
                    return true;
                }
                mongoDBManager.updatePlayerReputation(targetPlayer.getUniqueId(), points);
                player.sendMessage("Added " + points + " reputation points to " + targetPlayer.getName());
                break;

            case "remove":
                if (!sender.hasPermission("reputify.manage.remove")) {
                    sender.sendMessage("You don't have permission to remove reputation points.");
                    return true;
                }
                mongoDBManager.updatePlayerReputation(targetPlayer.getUniqueId(), -points);
                player.sendMessage("Removed " + points + " reputation points from " + targetPlayer.getName());
                break;

            case "set":
                if (!sender.hasPermission("reputify.manage.set")) {
                    sender.sendMessage("You don't have permission to set reputation points.");
                    return true;
                }
                mongoDBManager.updatePlayerReputation(targetPlayer.getUniqueId(), points);
                player.sendMessage("Set " + targetPlayer.getName() + "'s reputation points to " + points);
                break;

            default:
                player.sendMessage("Usage: /rep [player] [add|remove|set] <points>");
                break;
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            return null; // Tab completion only for players
        }

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // If one argument provided, suggest online players' names
            for (Player onlinePlayer : ((Player) sender).getServer().getOnlinePlayers()) {
                completions.add(onlinePlayer.getName());
            }
        }

        return completions;
    }
}
