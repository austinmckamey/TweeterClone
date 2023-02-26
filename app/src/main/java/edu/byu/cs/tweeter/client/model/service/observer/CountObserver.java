package edu.byu.cs.tweeter.client.model.service.observer;

import edu.byu.cs.tweeter.client.presenter.MainPresenter;

public class CountObserver implements ServiceObserver {

    private MainPresenter.MainView view;

    public CountObserver(MainPresenter.MainView view) {
        this.view = view;
    }

    public void handleSuccess(String type, int count) {
        if (type == null) {
            view.updateFollowersCount(0);
            view.updateFollowingCount(0);
        } else if (type.equals("followers")) {
            view.updateFollowersCount(count);
        } else if (type.equals("followees")) {
            view.updateFollowingCount(count);
        }
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
