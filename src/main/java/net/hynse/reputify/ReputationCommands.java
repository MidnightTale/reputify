package net.hynse.reputify;

import net.hynse.reputify.commands.AddRepCommand;
import net.hynse.reputify.commands.RemoveRepCommand;
import net.hynse.reputify.commands.SetRepCommand;
import net.hynse.reputify.commands.ViewRepCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReputationCommands implements CommandExecutor {

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

        if (args.length < 1) {
            // Display general command usage
            return true;
        }

        String targetPlayerName = args[0];
        Player targetPlayer = player.getServer().getPlayer(targetPlayerName);

        if (targetPlayer == null) {
            player.sendMessage("Player not found.");
            return true;
        }

        switch (command.getName().toLowerCase()) {
            case "setrep":
                SetRepCommand.execute(player, args, mongoDBManager);
                break;

            case "addrep":
                AddRepCommand.execute(player, args, mongoDBManager);
                break;

            case "removerep":
                RemoveRepCommand.execute(player, args, mongoDBManager);
                break;

            case "viewrep":
                ViewRepCommand.execute(player, args, mongoDBManager);
                break;
        }

        return true;
    }
}
