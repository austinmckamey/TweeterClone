package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of followers.
 */
public class GetFollowersTask extends PagedUserTask {

    public GetFollowersTask(AuthToken authToken, User targetUser, int limit, User lastFollower,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollower, messageHandler);
    }

    @Override
    protected final void runTask() throws IOException, TweeterRemoteException {
        String lastItemKey = "";
        if(lastItem != null) {
            lastItemKey = lastItem.getAlias();
        }
        FollowersRequest request = new FollowersRequest(Cache.getInstance().getCurrUserAuthToken(), targetUser.getAlias(), limit, lastItemKey);
        FollowersResponse response = facade.getFollowers(request, "/getfollowers");
        if(response.isSuccess()) {
            items = response.getFollowers();
            hasMorePages = response.getHasMorePages();
            sendSuccessMessage();
        } else {
            sendFailedMessage(response.getMessage());
        }
    }
}
