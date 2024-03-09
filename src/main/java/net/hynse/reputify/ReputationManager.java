package net.hynse.reputify;

import org.bson.Document;

import java.util.UUID;

public class ReputationManager {

    private final MongoDBManager mongoDBManager;

    public ReputationManager(MongoDBManager mongoDBManager) {
        this.mongoDBManager = mongoDBManager;
    }

    public Document getPlayerReputation(UUID playerId) {
        return mongoDBManager.getPlayerReputation(playerId);
    }

    public void updatePlayerReputation(UUID playerId, int newPoints) {
        mongoDBManager.updatePlayerReputation(playerId, newPoints);
    }
}
