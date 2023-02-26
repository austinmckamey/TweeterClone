package edu.byu.cs.tweeter.client.model.service.observer;

import java.util.List;

import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.model.domain.User;

public class GetUsersObserver implements PagedObserver<User> {

    PagedPresenter<User> presenter;
    PagedPresenter.PagedView<User> view;

    public GetUsersObserver(PagedPresenter<User> presenter, PagedPresenter.PagedView<User> view) {
        this.presenter = presenter;
        this.view = view;
    }

    @Override
    public void handleSuccess(List<User> followers, boolean hasMorePages, User lastFollower) {
        presenter.setLastItem(lastFollower);
        presenter.setHasMorePages(hasMorePages);

        view.setLoading(false);
        view.addItems(followers);
        presenter.setLoading(false);
    }

    @Override
    public void handleFailure(String message) {
        view.displayErrorMessage("",message);
    }

    @Override
    public void handleException(Exception exception) {
        //view.displayErrorMessage("",exception.getMessage());
    }
}
