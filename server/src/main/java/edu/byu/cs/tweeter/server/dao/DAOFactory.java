package edu.byu.cs.tweeter.server.dao;

public interface DAOFactory {
    UserDAO getUserDAO();
    FeedDAO getFeedDAO();
    StoryDAO getStoryDAO();
    FollowDAO getFollowDAO();
    AuthtokenDAO getAuthtokenDAO();
}
