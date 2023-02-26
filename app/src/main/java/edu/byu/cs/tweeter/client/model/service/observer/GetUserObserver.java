package edu.byu.cs.tweeter.client.model.service.observer;

import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUserObserver implements ServiceObserver {

    PagedPresenter.PagedView<?> view;

    public GetUserObserver(PagedPresenter.PagedView<?> view) {
        this.view = view;
    }

    public void handleSuccess(User user) {
        view.navigateToUser(user);
    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage("",message);
    }

    @Override
    public void handleException(Exception exception) {
        view.displayErrorMessage("",exception.getMessage());
    }
}
