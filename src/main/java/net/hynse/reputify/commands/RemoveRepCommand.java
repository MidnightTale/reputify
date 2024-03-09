package net.hynse.reputify.commands;

import net.hynse.reputify.MongoDBManager;
import org.bukkit.entity.Player;

public class RemoveRepCommand {

    public static void execute(Player player, String[] args, MongoDBManager mongoDBManager) {
        if (args.length < 2) {
            player.sendMessage("Usage: /removerep <player> <amount>");
            return;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            player.sendMessage("Player not found.");
            return;
        }

        int removeAmount = Integer.parseInt(args[1]);
        int currentRep = mongoDBManager.getPlayerReputation(targetPlayer.getUniqueId())
                .getInteger("reputation_points");
        int newRep = currentRep - removeAmount;
        mongoDBManager.updatePlayerReputation(targetPlayer.getUniqueId(), newRep);
        player.sendMessage("Removed " + removeAmount + " reputation from " + targetPlayer.getName());
    }
}
