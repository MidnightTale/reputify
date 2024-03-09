package net.hynse.reputify.commands;

import net.hynse.reputify.MongoDBManager;
import org.bukkit.entity.Player;

public class ViewRepCommand {

    public static void execute(Player player, String[] args, MongoDBManager mongoDBManager) {
        if (args.length < 1) {
            player.sendMessage("Usage: /viewrep <player>");
            return;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            player.sendMessage("Player not found.");
            return;
        }

        int targetRep = mongoDBManager.getPlayerReputation(targetPlayer.getUniqueId())
                .getInteger("reputation_points");
        player.sendMessage(targetPlayer.getName() + "'s reputation: " + targetRep);
    }
}
