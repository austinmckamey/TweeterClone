package edu.byu.cs.tweeter.model.net.response;

public class IsFollowerResponse extends Response {

    private boolean follower;

    public IsFollowerResponse(String message) {
        super(false, message);
    }

    public IsFollowerResponse(Boolean isFollower) {
        super(true, null);
        this.follower = isFollower;
    }

    public boolean getFollower() {
        return follower;
    }
}
