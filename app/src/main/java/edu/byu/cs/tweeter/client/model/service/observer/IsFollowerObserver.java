package edu.byu.cs.tweeter.client.model.service.observer;

import edu.byu.cs.tweeter.client.presenter.MainPresenter;

public class IsFollowerObserver implements ServiceObserver {

    private MainPresenter.MainView view;

    public IsFollowerObserver(MainPresenter.MainView view) {
        this.view = view;
    }

    public void handleSuccess(boolean isFollower) {
        view.updateIsFollower(isFollower);
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
