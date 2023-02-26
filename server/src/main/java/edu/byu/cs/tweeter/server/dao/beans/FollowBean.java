package edu.byu.cs.tweeter.server.dao.beans;
import edu.byu.cs.tweeter.server.dao.DBFollowDAO;
import edu.byu.cs.tweeter.server.dao.DBUserDAO;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class FollowBean {
    private String followee_handle;
    private String followee_name;
    private String follower_handle;
    private String follower_name;

    public FollowBean() {}

    public FollowBean(String followee_handle, String follower_handle, String followee_name, String follower_name) {
        this.follower_handle = follower_handle;
        this.followee_handle = followee_handle;
        this.followee_name = followee_name;
        this.follower_name = follower_name;
    }

    @DynamoDbPartitionKey
    @DynamoDbSecondarySortKey(indexNames = DBUserDAO.IndexName)
    public String getFollower_handle() {
        return followee_handle;
    }

    public void setFollowee_handle(String followee_handle) {
        this.followee_handle = followee_handle;
    }

    @DynamoDbSortKey
    @DynamoDbSecondaryPartitionKey(indexNames = DBUserDAO.IndexName)
    public String getFollowee_handle() {
        return follower_handle;
    }

    public void setFollower_handle(String follower_handle) {
        this.follower_handle = follower_handle;
    }

    public String getFollowee_name() {
        return followee_name;
    }

    public void setFollowee_name(String followee_name) {
        this.followee_name = followee_name;
    }

    public String getFollower_name() {
        return follower_name;
    }

    public void setFollower_name(String follower_name) {
        this.follower_name = follower_name;
    }

    @Override
    public String toString() {
        return "Follow{" +
                "followee='" + followee_handle + '\'' +
                ", follower='" + follower_handle + '\'' +
                '}';
    }
}
