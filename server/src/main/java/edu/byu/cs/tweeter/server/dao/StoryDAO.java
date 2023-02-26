package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.server.dao.beans.StoryBean;

public interface StoryDAO extends DAO {
    List<StoryBean> getStory(String key, String last);
    StoryBean getStatus(String key);
    void putStory(StoryBean story);
    void updateStory(StoryBean story, String key);
    void deleteStory(String key);
    boolean hasMorePages();
}
