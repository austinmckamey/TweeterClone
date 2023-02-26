package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.AuthenticationObserver;

public class LoginPresenter extends AuthenticationPresenter {

    public LoginPresenter(AuthenticationView view) {
        super(view);
    }

    public void initiateLogin(String username, String password) {
        String message = validateLogin(username,password);
        if(message == null) {
            view.clearErrorMessage("");
            view.displayInfoMessage("","Logging In ...");
            new UserService().login(username, password, new AuthenticationObserver(this,view));
        } else {
            view.clearInfoMessage("");
            view.displayErrorMessage("",message);
        }
    }

    public String validateLogin(String username, String password) {
        if(username.length() == 0) {
            return "Alias cannot be empty.";
        }
        if (username.charAt(0) != '@') {
            return "Alias must begin with @.";
        }
        if (username.length() < 2) {
            return "Alias must contain 1 or more characters after the @.";
        }
        if (password.length() == 0) {
            return "Password cannot be empty.";
        }
        return null;
    }
}
