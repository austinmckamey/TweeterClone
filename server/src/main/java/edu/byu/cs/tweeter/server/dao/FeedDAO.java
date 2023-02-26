package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.server.dao.beans.FeedBean;

public interface FeedDAO extends DAO {
    void addFeedBatch(List<String> followers, String sender, String timestamp, String post_body, List<String> urls, List<String> mentions);
    List<FeedBean> getFeed(String key, String last);
    FeedBean getStatus(String key);
    void putFeed(FeedBean Feed);
    void updateFeed(FeedBean Feed, String key);
    void deleteFeed(String key);
    boolean hasMorePages();
}
