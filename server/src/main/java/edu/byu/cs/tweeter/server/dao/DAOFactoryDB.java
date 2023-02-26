package edu.byu.cs.tweeter.server.dao;

public class DAOFactoryDB implements DAOFactory {

    private final FollowDAO followDAO;
    private final FeedDAO feedDAO;
    private final StoryDAO storyDAO;
    private final UserDAO userDAO;
    private final AuthtokenDAO authtokenDAO;

    public DAOFactoryDB() {
        followDAO = new DBFollowDAO();
        feedDAO = new DBFeedDAO();
        storyDAO = new DBStoryDAO();
        userDAO = new DBUserDAO();
        authtokenDAO = new DBAuthtokenDAO();
    }

    @Override
    public FollowDAO getFollowDAO() {
        return followDAO;
    }

    @Override
    public FeedDAO getFeedDAO() {
        return feedDAO;
    }

    @Override
    public StoryDAO getStoryDAO() {
        return storyDAO;
    }

    @Override
    public UserDAO getUserDAO() {
        return userDAO;
    }

    @Override
    public AuthtokenDAO getAuthtokenDAO() {
        return authtokenDAO;
    }
}
