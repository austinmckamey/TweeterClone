package edu.byu.cs.tweeter.client.view;

public interface MessageView extends View {
    void displayInfoMessage(String type,String message);
    void clearInfoMessage(String type);
    void clearErrorMessage(String type);
}
