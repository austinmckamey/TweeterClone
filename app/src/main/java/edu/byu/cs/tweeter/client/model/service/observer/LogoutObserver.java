package edu.byu.cs.tweeter.client.model.service.observer;

import edu.byu.cs.tweeter.client.presenter.MainPresenter;

public class LogoutObserver implements VoidObserver {

    MainPresenter.MainView view;

    public LogoutObserver(MainPresenter.MainView view) {
        this.view = view;
    }

    @Override
    public void handleFailure(String message) {
        view.clearInfoMessage("logout");
        view.displayErrorMessage("logout", message);
    }

    @Override
    public void handleException(Exception exception) {
        view.clearInfoMessage("logout");
        view.displayErrorMessage("logout", exception.getMessage());
    }

    @Override
    public void handleSuccess() {
        view.displayInfoMessage("logout", "Goodbye!");
        view.clearErrorMessage("logout");

        view.logoutUser();
    }
}
