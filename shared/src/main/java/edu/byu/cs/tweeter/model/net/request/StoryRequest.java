package edu.byu.cs.tweeter.model.net.request;

import edu.byu.cs.tweeter.model.domain.AuthToken;

public class StoryRequest {

    private AuthToken authToken;
    private String userAlias;
    private int limit;
    private String lastStatus;

    private StoryRequest() {}

    public StoryRequest(AuthToken authToken, String userAlias, int limit, String status) {
        this.authToken = authToken;
        this.userAlias = userAlias;
        this.limit = limit;
        this.lastStatus = status;
    }

    public AuthToken getAuthToken() {
        return authToken;
    }

    public void setAuthToken(AuthToken authToken) {
        this.authToken = authToken;
    }

    public String getUserAlias() {
        return userAlias;
    }

    public void setUserAlias(String userAlias) {
        this.userAlias = userAlias;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String status) {
        this.lastStatus = status;
    }
}
