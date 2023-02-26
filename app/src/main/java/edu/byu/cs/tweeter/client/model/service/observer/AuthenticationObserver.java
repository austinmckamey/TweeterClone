package edu.byu.cs.tweeter.client.model.service.observer;

import edu.byu.cs.tweeter.client.presenter.AuthenticationPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class AuthenticationObserver implements ServiceObserver {

    AuthenticationPresenter presenter;
    AuthenticationPresenter.AuthenticationView view;
    public AuthenticationObserver(AuthenticationPresenter presenter, AuthenticationPresenter.AuthenticationView view) {
        this.presenter = presenter;
        this.view = view;
    }

    public void handleSuccess(User user, AuthToken authToken) {
        view.clearInfoMessage("");
        view.clearErrorMessage("");

        view.displayInfoMessage("","Hello " + user.getName());
        view.navigateToUser(user);
    }

    @Override
    public void handleFailure(String message) {
        view.clearErrorMessage("");
        view.displayErrorMessage("",message);
    }

    @Override
    public void handleException(Exception exception) {
        view.clearErrorMessage("");
        view.displayErrorMessage("",exception.getMessage());
    }
}
