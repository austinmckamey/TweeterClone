package edu.byu.cs.tweeter.model.net.response;

public class CountResponse extends Response {

    private int count;
    private String type;

    CountResponse(boolean success, int count, String type) {
        super(success);
        this.count = count;
        this.type = type;
    }

    CountResponse(boolean success, String message, int count, String type) {
        super(success, message);
        this.count = count;
        this.type = type;
    }

    public int getCount() {
        return count;
    }

    public String getType() {
        return type;
    }
}
