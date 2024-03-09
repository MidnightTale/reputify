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

    public String getPlayerPrefix(UUID playerId) {
        Document playerReputation = getPlayerReputation(playerId);

        if (playerReputation.containsKey("positive_reputation") && playerReputation.containsKey("negative_reputation")) {
            int positiveReputationLevel = calculateReputationLevel(playerReputation.getInteger("positive_reputation"));
            int negativeReputationLevel = calculateReputationLevel(playerReputation.getInteger("negative_reputation"));

            switch (positiveReputationLevel) {
                case 1:
                    return "Newcomer";
                case 2:
                    return "Respected";
                case 3:
                    return "Veteran";
            }

            switch (negativeReputationLevel) {
                case 1:
                    return "Troublemaker";
                case 2:
                    return "Outcast";
                case 3:
                    return "Pariah";
            }
        }

        return "Player";
    }
    public static int calculateReputationLevel(int reputationPoints) {
        if (reputationPoints >= 7) {
            return 3; // Highest reputation level
        } else if (reputationPoints >= 3) {
            return 2; // Medium reputation level
        } else if (reputationPoints >= 1) {
            return 1; // Lowest reputation level
        }
        return reputationPoints;
    }
}
