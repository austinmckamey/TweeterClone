package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.observer.GetUsersObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public class FollowersPresenter extends PagedPresenter<User> {

    private static final String LOG_TAG = "FollowingPresenter";
    public FollowersPresenter(PagedView<User> view, User user) {
        super(view, user);
    }

    @Override
    public void getItems(AuthToken authToken, User user, int pageSize, User lastItem) {
        FollowService service = new FollowService();
        service.getFollowers(authToken,user,pageSize,lastItem,new GetUsersObserver(this,view));
    }
}
