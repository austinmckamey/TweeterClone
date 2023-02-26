package edu.byu.cs.tweeter.client.model.service.observer;

import java.util.List;

import edu.byu.cs.tweeter.client.presenter.PagedPresenter;
import edu.byu.cs.tweeter.model.domain.Status;

public class GetStatusesObserver implements PagedObserver<Status> {

    private final PagedPresenter<Status> presenter;
    private final PagedPresenter.PagedView<Status> view;

    public GetStatusesObserver(PagedPresenter<Status> presenter, PagedPresenter.PagedView<Status> view) {
        this.presenter = presenter;
        this.view = view;
    }

    @Override
    public void handleSuccess(List<Status> statuses, boolean hasMorePages, Status lastStatus) {
        view.displayInfoMessage("", "Story retrieved successfully!");
        view.clearErrorMessage("");

        presenter.setLastItem(lastStatus);
        presenter.setHasMorePages(hasMorePages);

        view.setLoading(false);
        view.addItems(statuses);
        presenter.setLoading(false);
    }

    @Override
    public void handleFailure(String message) {
        view.setLoading(false);

        view.displayErrorMessage("",message);
        presenter.setLoading(false);
    }

    @Override
    public void handleException(Exception exception) {
        view.setLoading(false);

        view.displayErrorMessage("",exception.getMessage());
        presenter.setLoading(false);
    }
}
