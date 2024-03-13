package net.hynse.reputify;

import org.bson.Document;

import java.util.UUID;

public class ReputationManager {

    private final MongoDBManager mongoDBManager;

    public ReputationManager(MongoDBManager mongoDBManager) {
        this.mongoDBManager = mongoDBManager;
    }

    public Document getPlayerReputation(UUID playerId) {
        Document playerReputation = mongoDBManager.getPlayerReputation(playerId);
        if (playerReputation == null) {
            // If player's reputation is not found, create a new document with default values
            playerReputation = new Document("uuid", playerId.toString())
                    .append("reputation_points", 0);
            // You can add more default fields if needed
        }
        return playerReputation;
    }

    public void updatePlayerReputation(UUID playerId, int newPoints) {
        mongoDBManager.updatePlayerReputation(playerId, newPoints);
    }
}
