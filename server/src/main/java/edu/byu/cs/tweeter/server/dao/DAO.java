package edu.byu.cs.tweeter.server.dao;

public interface DAO {
    String AuthTableName = "authtoken";
    String FeedTableName = "feed";
    String StoryTableName = "story";
    String UserTableName = "user";
    String FollowTableName = "follows";
}
