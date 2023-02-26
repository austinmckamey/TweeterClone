package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;
import java.util.List;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FeedRequest;
import edu.byu.cs.tweeter.model.net.response.FeedResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that retrieves a page of statuses from a user's feed.
 */
public class GetFeedTask extends PagedStatusTask {

    public GetFeedTask(AuthToken authToken, User targetUser, int limit, Status lastStatus,
                       Handler messageHandler) {
        super(authToken, targetUser, limit, lastStatus, messageHandler);
    }

    @Override
    protected final void runTask() throws IOException, TweeterRemoteException {
        String lastItemKey = "";
        if(lastItem != null) {
            lastItemKey = lastItem.getDate();
        }
        FeedRequest request = new FeedRequest(Cache.getInstance().getCurrUserAuthToken(), targetUser.getAlias(), limit, lastItemKey);
        FeedResponse response = facade.getFeed(request, "/getfeed");
        if(response.isSuccess()) {
            items = response.getFeed();
            hasMorePages = response.getHasMorePages();
            sendSuccessMessage();
        } else {
            sendFailedMessage(response.getMessage());
        }
    }
}
