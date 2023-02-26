package edu.byu.cs.tweeter.server.dao;

import java.util.List;

import edu.byu.cs.tweeter.server.dao.beans.FollowBean;
import software.amazon.awssdk.core.pagination.sync.SdkIterable;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.PageIterable;

public interface FollowDAO extends DAO {
    SdkIterable<Page<FollowBean>> getFollowersForPost(String key);
    List<FollowBean> getFollowers(String key, String last, int limit);
    List<FollowBean> getFollowees(String key, String last, int limit);
    int getFollowersCount(String key);
    int getFollowingCount(String key);
    FollowBean getFollow(String key);
    FollowBean getFollowSort(String key, String sort);
    void putFollow(FollowBean follow);
    void updateFollow(FollowBean follow, String key);
    void deleteFollow(String key, String sort);
    boolean hasMorePages();
}
