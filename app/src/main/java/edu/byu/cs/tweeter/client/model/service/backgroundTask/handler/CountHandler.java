package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.backgroundTask.GetCountTask;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;

public class CountHandler extends BackgroundTaskHandler<CountObserver> {
    public CountHandler(CountObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(CountObserver observer, Bundle data) {
        int count = data.getInt(GetCountTask.COUNT_KEY);
        String type = data.getString(GetCountTask.COUNT_TYPE);
        observer.handleSuccess(type,count);
    }
}
