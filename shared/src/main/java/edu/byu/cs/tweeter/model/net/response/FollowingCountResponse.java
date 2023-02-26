package edu.byu.cs.tweeter.model.net.response;

public class FollowingCountResponse extends CountResponse {

    public FollowingCountResponse(String message) {
        super(false, message, 0, null);
    }

    public FollowingCountResponse(int count, String type) {
        super(true, null, count, type);
    }
}
