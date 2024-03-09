package net.hynse.reputify;

import me.nahu.scheduler.wrapper.FoliaWrappedJavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;

import java.util.Objects;
import java.util.logging.Level;

public class Reputify extends FoliaWrappedJavaPlugin {

    public static Reputify instance;

    @Override
    public void onEnable() {
        instance = this;

        // Load MongoDB's connection details from config.yml
        MongoDBConfig mongoDBConfig = loadMongoDBConfig();

        // Initialize MongoDBManager
        MongoDBManager mongoDBManager = new MongoDBManager(
                mongoDBConfig.getConnectionString(),
                mongoDBConfig.getDatabaseName(),
                mongoDBConfig.getCollectionName()
        );
        ReputationManager reputationManager = new ReputationManager(mongoDBManager);

        // Check if PlaceholderAPI is enabled
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            // Pass ReputationManager instance to ReputifyPlaceholderExpansion constructor
            new ReputifyPlaceholderExpansion(reputationManager).register();
            getLogger().log(Level.INFO, "PlaceholderAPI Hooked");
        } else {
            getLogger().log(Level.WARNING, "PlaceholderAPI not found. Some features may not work.");
        }

        // Register events and commands
        Objects.requireNonNull(getCommand("setrep")).setExecutor(new ReputationCommands(mongoDBManager));
        Objects.requireNonNull(getCommand("addrep")).setExecutor(new ReputationCommands(mongoDBManager));
        Objects.requireNonNull(getCommand("removerep")).setExecutor(new ReputationCommands(mongoDBManager));
        Objects.requireNonNull(getCommand("viewrep")).setExecutor(new ReputationCommands(mongoDBManager));
        Objects.requireNonNull(getCommand("tellrep")).setExecutor(new ReputationCommands(mongoDBManager));
        getServer().getPluginManager().registerEvents(new EventListener(reputationManager), this);

        getLogger().log(Level.INFO, "Reputify has been enabled.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
        getLogger().log(Level.INFO, "Reputify has been disabled.");
    }

    private MongoDBConfig loadMongoDBConfig() {
        // Load MongoDB connection details from config.yml
        FileConfiguration config = getConfig();
        String connectionString = config.getString("mongodb.connectionString", "");
        String databaseName = config.getString("mongodb.databaseName", "");
        String collectionName = config.getString("mongodb.collectionName", "");

        return new MongoDBConfig(connectionString, databaseName, collectionName);
    }
}
