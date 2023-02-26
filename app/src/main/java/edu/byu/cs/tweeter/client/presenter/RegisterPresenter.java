package edu.byu.cs.tweeter.client.presenter;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.AuthenticationObserver;

public class RegisterPresenter extends AuthenticationPresenter {

    public RegisterPresenter(AuthenticationView view) {
        super(view);
    }

    public void initiateImageUpload() {
        view.uploadImage();
    }

    public void initiateRegister(String firstName, String lastName, String username, String password, String imageBytesBase64) {
        String message = validateRegistration(firstName, lastName, username, password);
        if (message == null) {
            view.clearErrorMessage("");
            view.displayInfoMessage("","Logging In ...");
            new UserService().register(firstName,lastName,username,password,imageBytesBase64,new AuthenticationObserver(this,view));
        }
        else {
            view.clearInfoMessage("");
            view.displayErrorMessage("",message);
        }
    }

    public String validateRegistration(String firstName, String lastName, String username, String password) {
        if (firstName.length() == 0) {
            return "First Name cannot be empty.";
        }
        if (lastName.length() == 0) {
            return "Last Name cannot be empty.";
        }
        if (username.length() == 0) {
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
