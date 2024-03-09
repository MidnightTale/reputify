package net.hynse.reputify;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class MongoDBManager {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> reputationCollection;

    // Map to store recent kills
    private Map<UUID, Map<UUID, Long>> recentKills = new HashMap<>();

    public MongoDBManager(String connectionString, String databaseName, String collectionName) {
        // Create MongoDB connection
        mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        reputationCollection = database.getCollection(collectionName);
    }

    public Document getPlayerReputation(UUID playerId) {
        // Retrieve player's reputation document from MongoDB
        return reputationCollection.find(eq("player_id", playerId.toString())).first();
    }

    public void insertPlayerReputation(UUID playerId) {
        // Insert new player's reputation into MongoDB
        Document document = new Document("player_id", playerId.toString())
                .append("reputation_points", 0);

        reputationCollection.insertOne(document);
    }

    public void updatePlayerReputation(UUID playerId, int newPoints) {
        // Retrieve player's current reputation document
        Document playerDocument = reputationCollection.find(eq("player_id", playerId.toString())).first();

        // Extract previous reputation points
        int previousPoints = playerDocument.getInteger("reputation_points");

        // Prepare document for update
        Document updateDocument = new Document("$set", new Document("reputation_points", newPoints));

        // Get the current timestamp
        LocalDateTime currentTime = LocalDateTime.now();

        // Add transition information
        updateDocument.append("$push", new Document("transitions", new Document("previous_points", previousPoints)
                .append("new_points", newPoints)
                .append("timestamp", currentTime.toString())));

        // Update player's reputation points in MongoDB
        reputationCollection.updateOne(
                eq("player_id", playerId.toString()),
                updateDocument,
                new UpdateOptions().upsert(true)
        );
    }

    // Initialize recent kills map for a player
    public void initializeRecentKillsMap(UUID playerId) {
        recentKills.put(playerId, new HashMap<>());
    }

    // Check if a recent kill exists
    public boolean hasRecentKill(UUID playerId, UUID victimId, long cooldownPeriod) {
        if (recentKills.containsKey(playerId) && recentKills.get(playerId).containsKey(victimId)) {
            long lastKillTime = recentKills.get(playerId).get(victimId);
            long currentTime = System.currentTimeMillis();
            return currentTime - lastKillTime < cooldownPeriod;
        }
        return false;
    }

    // Add a recent kill
    public void addRecentKill(UUID playerId, UUID victimId) {
        recentKills.computeIfAbsent(playerId, k -> new HashMap<>());
        recentKills.get(playerId).put(victimId, System.currentTimeMillis());
    }
    public long getRecentKillTime(UUID playerId, UUID victimId) {
        if (recentKills.containsKey(playerId) && recentKills.get(playerId).containsKey(victimId)) {
            return recentKills.get(playerId).get(victimId);
        }
        return 0; // Return 0 if no recent kill time is found
    }

}
