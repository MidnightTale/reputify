package net.hynse.reputify;

import me.nahu.scheduler.wrapper.WrappedScheduler;
import me.nahu.scheduler.wrapper.WrappedSchedulerBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Reputify extends JavaPlugin {

    private MongoDBManager mongoDBManager;
    private static Reputify instance;

    public static @NotNull WrappedScheduler getInstance() {
        return instance;
    }
    final WrappedSchedulerBuilder schedulerBuilder = WrappedSchedulerBuilder.builder().plugin(getPlugin(Reputify.class));
    final WrappedScheduler scheduler = schedulerBuilder.build();

    @Override
    public void onEnable() {
        instance = this;
        // Load MongoDB's connection details from config.yml
        MongoDBConfig mongoDBConfig = loadMongoDBConfig();

        // Initialize MongoDBManager
        mongoDBManager = new MongoDBManager(
                mongoDBConfig.getConnectionString(),
                mongoDBConfig.getDatabaseName(),
                mongoDBConfig.getCollectionName()
        );

        // Register events
        Objects.requireNonNull(getCommand("setrep")).setExecutor(new ReputationCommands(mongoDBManager));
        Objects.requireNonNull(getCommand("addrep")).setExecutor(new ReputationCommands(mongoDBManager));
        Objects.requireNonNull(getCommand("removerep")).setExecutor(new ReputationCommands(mongoDBManager));
        Objects.requireNonNull(getCommand("viewrep")).setExecutor(new ReputationCommands(mongoDBManager));
        Objects.requireNonNull(getCommand("tellrep")).setExecutor(new ReputationCommands(mongoDBManager));
        getServer().getPluginManager().registerEvents(new EventListener(mongoDBManager), this);
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
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