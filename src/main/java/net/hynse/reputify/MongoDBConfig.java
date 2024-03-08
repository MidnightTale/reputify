package net.hynse.reputify;

public class MongoDBConfig {

    private final String connectionString;
    private final String databaseName;
    private final String collectionName;

    public MongoDBConfig(String connectionString, String databaseName, String collectionName) {
        this.connectionString = connectionString;
        this.databaseName = databaseName;
        this.collectionName = collectionName;
    }

    public String getConnectionString() {
        return connectionString;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getCollectionName() {
        return collectionName;
    }
}