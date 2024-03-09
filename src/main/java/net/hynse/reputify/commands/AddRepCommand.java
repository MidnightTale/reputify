package net.hynse.reputify.commands;

import net.hynse.reputify.MongoDBManager;
import org.bukkit.entity.Player;

public class AddRepCommand {

    public static void execute(Player player, String[] args, MongoDBManager mongoDBManager) {
        if (args.length < 2) {
            player.sendMessage("Usage: /addrep <player> <amount>");
            return;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            player.sendMessage("Player not found.");
            return;
        }

        int addAmount = Integer.parseInt(args[1]);
        int currentReputation = mongoDBManager.getPlayerReputation(targetPlayer.getUniqueId())
                .getInteger("reputation_points");
        int newReputation = currentReputation + addAmount;
        mongoDBManager.updatePlayerReputation(targetPlayer.getUniqueId(), newReputation);
        player.sendMessage("Added " + addAmount + " reputation to " + targetPlayer.getName());
    }
}
