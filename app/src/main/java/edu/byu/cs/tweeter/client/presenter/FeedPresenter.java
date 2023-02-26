package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.GetStatusesObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class FeedPresenter extends PagedPresenter<Status> {

    private static final String LOG_TAG = "FeedPresenter";

    public FeedPresenter(PagedView<Status> view, User user) {
        super(view, user);
    }

    @Override
    public void getItems(AuthToken authToken, User user, int pageSize, Status lastItem) {
        StatusService service = new StatusService();
        service.getFeed(user, pageSize, lastItem, new GetStatusesObserver(this, view));
    }
}
