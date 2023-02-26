package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFollowersTask;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class PagedUserHandler extends BackgroundTaskHandler<PagedObserver<User>>{
    public PagedUserHandler(PagedObserver<User> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedObserver<User> observer, Bundle data) {
        List<User> list = (List<User>) data.getSerializable(GetFollowersTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetFollowersTask.MORE_PAGES_KEY);
        User object;
        if (list != null) {
            object = (list.size() > 0) ? list.get(list.size() - 1) : null;
        } else {
            object = null;
        }
        observer.handleSuccess(list,hasMorePages,object);
    }
}
