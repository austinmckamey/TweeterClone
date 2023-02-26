package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetUserTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LoginTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.LogoutTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.RegisterTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.AuthenticationHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.GetUserHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.VoidHandler;
import edu.byu.cs.tweeter.client.model.service.observer.AuthenticationObserver;
import edu.byu.cs.tweeter.client.model.service.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.model.service.observer.VoidObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;

public class UserService {

    public void login(String username, String password, AuthenticationObserver observer) {
        LoginTask loginTask = new LoginTask(username, password, new AuthenticationHandler(observer));
        BackgroundTaskUtils.runTask(loginTask);
    }

    public void register(String firstName, String lastName, String username, String password,
                         String imageBytesBase64, AuthenticationObserver observer) {
        RegisterTask registerTask = new RegisterTask(firstName, lastName, username, password,
                imageBytesBase64, new AuthenticationHandler(observer));
        BackgroundTaskUtils.runTask(registerTask);
    }

    public void getUser(String user, GetUserObserver observer) {
        GetUserTask getUserTask = new GetUserTask(Cache.getInstance().getCurrUserAuthToken(),
                user, new GetUserHandler(observer));
        BackgroundTaskUtils.runTask(getUserTask);
    }

    public void logout(AuthToken authToken, VoidObserver observer) {
        LogoutTask logoutTask = new LogoutTask(authToken, new VoidHandler(observer));
        BackgroundTaskUtils.runTask(logoutTask);
    }
}
