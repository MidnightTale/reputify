package net.hynse.reputify;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ReputationCommands implements CommandExecutor {

    private final MongoDBManager mongoDBManager;

    public ReputationCommands(MongoDBManager mongoDBManager) {
        this.mongoDBManager = mongoDBManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use these commands.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length < 1) {
            player.sendMessage("Usage: /setrep <player> <amount>");
            player.sendMessage("Usage: /addrep <player> <amount>");
            player.sendMessage("Usage: /removerep <player> <amount>");
            player.sendMessage("Usage: /viewrep <player>");
            player.sendMessage("Usage: /tellrep <player>");

            return true;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            player.sendMessage("Player not found.");
            return true;
        }

        UUID targetPlayerId = targetPlayer.getUniqueId();

        switch (command.getName().toLowerCase()) {
            case "setrep":
                if (args.length < 2) {
                    player.sendMessage("Usage: /setrep <player> <amount>");
                    return true;
                }
                int setAmount = Integer.parseInt(args[1]);
                mongoDBManager.updatePlayerReputation(targetPlayerId, setAmount);
                player.sendMessage("Set " + targetPlayer.getName() + "'s reputation to " + setAmount);
                break;

            case "addrep":
                if (args.length < 2) {
                    player.sendMessage("Usage: /addrep <player> <amount>");
                    return true;
                }
                int addAmount = Integer.parseInt(args[1]);
                int currentReputation = mongoDBManager.getPlayerReputation(targetPlayerId)
                        .getInteger("reputation_points");
                int newReputation = currentReputation + addAmount;
                mongoDBManager.updatePlayerReputation(targetPlayerId, newReputation);
                player.sendMessage("Added " + addAmount + " reputation to " + targetPlayer.getName());
                break;

            case "removerep":
                if (args.length < 2) {
                    player.sendMessage("Usage: /removerep <player> <amount>");
                    return true;
                }
                int removeAmount = Integer.parseInt(args[1]);
                int currentRep = mongoDBManager.getPlayerReputation(targetPlayerId)
                        .getInteger("reputation_points");
                int newRep = currentRep - removeAmount;
                mongoDBManager.updatePlayerReputation(targetPlayerId, newRep);
                player.sendMessage("Removed " + removeAmount + " reputation from " + targetPlayer.getName());
                break;

            case "viewrep":
                int targetRep = mongoDBManager.getPlayerReputation(targetPlayerId)
                        .getInteger("reputation_points");
                player.sendMessage(targetPlayer.getName() + "'s reputation: " + targetRep);
                break;

            case "tellrep":
                int tellRep = mongoDBManager.getPlayerReputation(targetPlayerId)
                        .getInteger("reputation_points");
                targetPlayer.sendMessage("Your reputation: " + tellRep);
                break;

        }


        return true;

    }

}