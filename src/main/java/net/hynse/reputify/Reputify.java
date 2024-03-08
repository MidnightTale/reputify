package net.hynse.reputify;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class Reputify extends JavaPlugin {

    private MongoDBManager mongoDBManager;

    @Override
    public void onEnable() {
        // Load configuration from file
        Properties config = loadConfig();

        // MongoDB's connection details
        String connectionString = config.getProperty("mongodb.connectionString");
        String databaseName = config.getProperty("mongodb.databaseName");
        String collectionName = config.getProperty("mongodb.collectionName");

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
        mongoDBManager.closeConnection();
        HandlerList.unregisterAll(this);
    }

    private Properties loadConfig() {
        Properties properties = new Properties();
        try (InputStream input = getResource("config.properties")) {
            properties.load(input);
        } catch (Exception e) {
            getLogger().severe("Error loading configuration: " + e.getMessage());
        }
        return properties;
    }
}
