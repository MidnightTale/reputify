package net.hynse.reputify;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class Reputify extends JavaPlugin {

    private MongoDBManager mongoDBManager;

    @Override
    public void onEnable() {
        // Initialize MongoDB's connection details with default values
        String connectionString = "mongodb://username:password@localhost:27017/?authSource=admin";
        String databaseName = "DatabaseName";
        String collectionName = "Reputify";

        // Load configuration from file if present
        FileConfiguration config = loadConfig();
        connectionString = config.getString("mongodb.connectionString", connectionString);
        databaseName = config.getString("mongodb.databaseName", databaseName);
        collectionName = config.getString("mongodb.collectionName", collectionName);

        // Initialize MongoDBManager
        mongoDBManager = new MongoDBManager(connectionString, databaseName, collectionName);

        // Initialize ReputationManager with an empty cache
        ReputationManager reputationManager = new ReputationManager(new HashMap<>());

        // Register events
        getServer().getPluginManager().registerEvents(new EventListener(mongoDBManager, reputationManager), this);
    }

    @Override
    public void onDisable() {
        // Close MongoDB connection and unregister events on plugin disable
        if (mongoDBManager != null) {
            mongoDBManager.closeConnection();
        }
        HandlerList.unregisterAll(this);
    }

    private FileConfiguration loadConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            // Save the default config.yml if it doesn't exist
            saveResource("config.yml", false);
        }

        return getConfig();
    }
}
