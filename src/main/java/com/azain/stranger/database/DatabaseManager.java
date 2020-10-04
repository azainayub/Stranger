package com.azain.stranger.database;

public interface DatabaseManager {
    DatabaseManager INSTANCE = new SQLiteDataSource();

    String getPrefix(long guildId);
    void setPrefix(long guildId, String newPrefix);
}