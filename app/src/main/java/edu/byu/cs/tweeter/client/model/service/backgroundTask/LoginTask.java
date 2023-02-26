package edu.byu.cs.tweeter.client.model.service.backgroundTask;

import android.os.Handler;

import java.io.IOException;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.util.Pair;

/**
 * Background task that logs in a user (i.e., starts a session).
 */
public class LoginTask extends AuthenticateTask {

    public LoginTask(String username, String password, Handler messageHandler) {
        super(messageHandler, username, password);
    }

    @Override
    protected final void runTask() throws IOException, TweeterRemoteException {
        LoginRequest request = new LoginRequest(username, password);
        LoginResponse response = facade.login(request, "/login");

        if(response.isSuccess()) {
            authenticatedUser = response.getUser();
            authToken = response.getAuthToken();
            Cache.getInstance().setCurrUserAuthToken(authToken);
            sendSuccessMessage();
        } else {
            sendFailedMessage(response.getMessage());
        }
    }
}
