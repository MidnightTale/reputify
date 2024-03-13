package net.hynse.reputify;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class MongoDBManager {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> reputationCollection;

    public MongoDBManager(String connectionString, String databaseName, String collectionName) {
        mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        reputationCollection = database.getCollection(collectionName);
    }

    public Document getPlayerReputation(UUID playerId) {
        return reputationCollection.find(eq("player_id", playerId.toString())).first();
    }

    public void updatePlayerReputation(UUID playerId, int newPoints) {
        Document playerDocument = reputationCollection.find(eq("player_id", playerId.toString())).first();
        if (playerDocument == null) {
            // If player's document is not found, create a new document with default values
            playerDocument = new Document("player_id", playerId.toString())
                    .append("reputation_points", 0);
            // You can add more default fields if needed
        }
        int previousPoints = playerDocument.getInteger("reputation_points");
        int cappedPoints = Math.max(-100, Math.min(100, newPoints));
        Document updateDocument = new Document("$set", new Document("reputation_points", cappedPoints));
        LocalDateTime currentTime = LocalDateTime.now();
        updateDocument.append("$push", new Document("transitions", new Document("previous_points", previousPoints)
                .append("new_points", cappedPoints)
                .append("timestamp", currentTime.toString())));
        reputationCollection.updateOne(
                eq("player_id", playerId.toString()),
                updateDocument,
                new UpdateOptions().upsert(true)
        );
    }
}
