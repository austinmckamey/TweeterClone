package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class FollowRequest {

    private AuthToken authToken;
    private String followeeAlias;
    private String followerAlias;
    private String followeeName;
    private String followerName;

    private FollowRequest() {}

    public FollowRequest(AuthToken authToken, String followeeAlias, String followerAlias, String followeeName, String followerName) {
        this.authToken = authToken;
        this.followeeAlias = followeeAlias;
        this.followerAlias = followerAlias;
        this.followeeName = followeeName;
        this.followerName = followerName;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getFolloweeAlias() {
        return followeeAlias;
    }

    public void setFolloweeAlias(String followeeAlias) {
        this.followeeAlias = followeeAlias;
    }

    public String getFollowerAlias() {
        return followerAlias;
    }

    public void setFollowerAlias(String followerAlias) {
        this.followerAlias = followerAlias;
    }

    public String getFolloweeName() {
        return followeeName;
    }

    public void setFolloweeName(String followeeName) {
        this.followeeName = followeeName;
    }

    public String getFollowerName() {
        return followerName;
    }

    public void setFollowerName(String followerName) {
        this.followerName = followerName;
    }
}
