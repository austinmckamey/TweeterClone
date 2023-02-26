package edu.byu.cs.tweeter.client.model.service.observer;

import edu.byu.cs.tweeter.client.presenter.MainPresenter;

public class PostStatusObserver implements VoidObserver {

    MainPresenter.MainView view;

    public PostStatusObserver(MainPresenter.MainView view) {
        this.view = view;
    }

    @Override
    public void handleSuccess() {
        view.clearInfoMessage("posting");
        view.displayInfoMessage("posting", "Successfully posted!");
    }

    @Override
    public void handleFailure(String message) {
        view.clearInfoMessage("posting");
        view.displayErrorMessage("posting", "Failed to post status: " + message);
    }

    @Override
    public void handleException(Exception exception) {
        view.clearInfoMessage("posting");
        view.displayErrorMessage("posting", "Failed to post status because of exception: " + exception.getMessage());
    }
}
