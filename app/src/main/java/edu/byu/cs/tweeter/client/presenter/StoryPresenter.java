package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.GetStatusesObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StoryPresenter extends PagedPresenter<Status> {

    private static final String LOG_TAG = "StoryPresenter";

    private StatusService service;

    public StoryPresenter(PagedView<Status> view, User user) {
        super(view, user);
    }

    @Override
    public void getItems(AuthToken authToken, User user, int pageSize, Status lastItem) {
        view.displayInfoMessage("", "Retrieving Story...");
        service = getStatusService();
        service.getStory(user, pageSize, lastItem, new GetStatusesObserver(this, view));
    }

    public StatusService getStatusService() {
        if(service == null) {
            return new StatusService();
        }
        return service;
    }
}
