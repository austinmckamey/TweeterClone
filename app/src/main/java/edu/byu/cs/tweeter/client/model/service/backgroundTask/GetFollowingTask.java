package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingRequest;
import edu.byu.cs.tweeter.model.net.response.FollowersResponse;
import edu.byu.cs.tweeter.model.net.response.FollowingResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of other users being followed by a specified user.
 */
public class GetFollowingTask extends PagedUserTask {

    public GetFollowingTask(AuthToken authToken, User targetUser, int limit, User lastFollowee,
                            Handler messageHandler) {
        super(authToken, targetUser, limit, lastFollowee, messageHandler);
    }

    @Override
    protected final void runTask() throws IOException, TweeterRemoteException {
        String lastItemKey = "";
        if(lastItem != null) {
            lastItemKey = lastItem.getAlias();
        }
        FollowingRequest request = new FollowingRequest(Cache.getInstance().getCurrUserAuthToken(), targetUser.getAlias(), limit, lastItemKey);
        FollowingResponse response = facade.getFollowing(request, "/getfollowing");
        if(response.isSuccess()) {
            items = response.getFollowees();
            hasMorePages = response.getHasMorePages();
            sendSuccessMessage();
        } else {
            sendFailedMessage(response.getMessage());
        }
    }
}
