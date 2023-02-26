package edu.byu.cs.tweeter.client.model.service.backgroundTask.handler;

import android.os.Bundle;

import edu.byu.cs.tweeter.client.model.service.observer.VoidObserver;

public class VoidHandler extends BackgroundTaskHandler<VoidObserver> {

    public VoidHandler(VoidObserver observer) {
        super(observer);
    }

    @Override
    protected void handleSuccessMessage(VoidObserver observer, Bundle data) {
        observer.handleSuccess();
    }
}
