package edu.byu.cs.tweeter.client.model.service.observer;

import edu.byu.cs.tweeter.client.presenter.MainPresenter;

public class FollowObserver implements VoidObserver {

    MainPresenter.MainView view;
    boolean isAdded;

    public FollowObserver(MainPresenter.MainView view, boolean isAdded) {
        this.view = view;
        this.isAdded = isAdded;
    }

    @Override
    public void handleSuccess() {
        view.updateFollowAndUnfollow(isAdded);
    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage("other", message);
    }

    @Override
    public void handleException(Exception exception) {
        view.displayErrorMessage("other", exception.getMessage());
    }
}
