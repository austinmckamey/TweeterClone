package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetStoryTask;
import edu.byu.cs.tweeter.client.model.service.observer.PagedObserver;
import edu.byu.cs.tweeter.model.domain.Status;

public class PagedStatusHandler extends BackgroundTaskHandler<PagedObserver<Status>> {
    public PagedStatusHandler(PagedObserver<Status> observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(PagedObserver<Status> observer, Bundle data) {
        List<Status> list = (List<Status>) data.getSerializable(GetStoryTask.ITEMS_KEY);
        boolean hasMorePages = data.getBoolean(GetStoryTask.MORE_PAGES_KEY);
        Status object;
        if (list != null) {
            object = (list.size() > 0) ? list.get(list.size() - 1) : null;
        } else {
            object = null;
        }
        observer.handleSuccess(list,hasMorePages,object);
    }
}
