package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.FollowersCountRequest;
import edu.byu.cs.tweeter.model.net.request.FollowingCountRequest;

/**
 * Background task that queries how many other users a specified user is following.
 */
public class GetFollowingCountTask extends GetCountTask {

    public GetFollowingCountTask(AuthToken authToken, User targetUser, Handler messageHandler) {
        super(authToken, targetUser, messageHandler);
    }

    @Override
    protected void runTask() throws IOException, TweeterRemoteException {
        FollowingCountRequest request = new FollowingCountRequest(Cache.getInstance().getCurrUserAuthToken(), getTargetUser().getAlias());
        response = facade.getFollowingCount(request, "/getfollowingcount");
        if(response.isSuccess()) {
            count = response.getCount();
            type = response.getType();
            sendSuccessMessage();
        } else {
            sendFailedMessage(response.getMessage());
        }
    }
}
