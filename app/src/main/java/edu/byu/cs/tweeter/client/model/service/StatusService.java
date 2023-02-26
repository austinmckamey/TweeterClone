package edu.byu.cs.tweeter.client.model.service;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.BackgroundTaskUtils;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetFeedTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.PostStatusTask;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.PagedStatusHandler;
import edu.byu.cs.tweeter.client.model.service.backgroundTask.handler.VoidHandler;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.client.model.service.observer.VoidObserver;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class StatusService {

    public void getFeed(User targetUser, int limit, Status lastStatus, PagedObserver<Status> observer) {
        GetFeedTask getFeedTask = new GetFeedTask(Cache.getInstance().getCurrUserAuthToken(),
                targetUser, limit, lastStatus, new PagedStatusHandler(observer));
        BackgroundTaskUtils.runTask(getFeedTask);
    }
    public void getStory(User user, int limit, Status lastStatus, PagedObserver<Status> observer) {
        GetStoryTask getStoryTask = new GetStoryTask(Cache.getInstance().getCurrUserAuthToken(),
                user, limit, lastStatus, new PagedStatusHandler(observer));
        BackgroundTaskUtils.runTask(getStoryTask);
    }
    public void postStatus(AuthToken authToken, Status newStatus, VoidObserver observer) {
        PostStatusTask statusTask = new PostStatusTask(authToken,
                newStatus, new VoidHandler(observer));
        BackgroundTaskUtils.runTask(statusTask);
    }
}
