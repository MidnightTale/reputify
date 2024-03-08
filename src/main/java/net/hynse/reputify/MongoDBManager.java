/**
 * MongoDBManager Class
 * Manages MongoDB operations for player reputation.
 */
package net.hynse.reputify;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class MongoDBManager {

    private final MongoClient mongoClient;
    private final MongoCollection<Document> reputationCollection;

    public MongoDBManager(String connectionString, String databaseName, String collectionName) {
        // Create MongoDB connection
        mongoClient = MongoClients.create(connectionString);
        MongoDatabase database = mongoClient.getDatabase(databaseName);
        reputationCollection = database.getCollection(collectionName);
    }

    public void closeConnection() {
        // Close MongoDB connection
        if (mongoClient != null) {
            mongoClient.close();
        }
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
}
