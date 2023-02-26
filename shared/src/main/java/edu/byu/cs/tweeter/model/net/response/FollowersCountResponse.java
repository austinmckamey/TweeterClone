package edu.byu.cs.tweeter.model.net.response;

public class FollowersCountResponse extends CountResponse {

    public FollowersCountResponse(String message) {
        super(false, message, 0, null);
    }

    public FollowersCountResponse(int count, String type) {
        super(true, count, type);
    }
}
