package net.hynse.reputify.commands;

import net.hynse.reputify.MongoDBManager;
import org.bukkit.entity.Player;

public class SetRepCommand {

    public static void execute(Player player, String[] args, MongoDBManager mongoDBManager) {
        if (args.length < 2) {
            player.sendMessage("Usage: /setrep <player> <amount>");
            return;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            player.sendMessage("Player not found.");
            return;
        }

        int setAmount = Integer.parseInt(args[1]);
        mongoDBManager.updatePlayerReputation(targetPlayer.getUniqueId(), setAmount);
        player.sendMessage("Set " + targetPlayer.getName() + "'s reputation to " + setAmount);
    }
}
