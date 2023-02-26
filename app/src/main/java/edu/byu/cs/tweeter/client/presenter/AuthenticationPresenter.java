package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.view.MessageView;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class AuthenticationPresenter extends Presenter {

    public interface AuthenticationView extends MessageView {
        void navigateToUser(User user);
        void uploadImage();
    }

    protected AuthenticationView view;

    protected AuthenticationPresenter(AuthenticationView view) {
        this.view = view;
    }
}
