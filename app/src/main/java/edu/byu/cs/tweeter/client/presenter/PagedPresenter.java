package edu.byu.cs.tweeter.client.presenter;

import java.util.List;

import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.GetUserObserver;
import edu.byu.cs.tweeter.client.view.MessageView;
import edu.byu.cs.tweeter.client.view.View;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.User;

public abstract class PagedPresenter<T> extends Presenter {

    public interface PagedView<U> extends MessageView {
        void setLoading(boolean isLoading);
        void addItems(List<U> items);
        void navigateToUser(User user);
        void navigateToURL(String url);
    }

    public static final int PAGE_SIZE = 10;

    protected PagedView<T> view;
    private User user;
    private AuthToken authToken;
    private T lastItem;
    private boolean hasMorePages = true;
    private boolean isLoading = false;
    private boolean isGettingUser = false;

    protected PagedPresenter(PagedView<T> view, User user) {
        this.view = view;
        this.user = user;
    }

    public T getLastItem() {
        return lastItem;
    }

    public void setLastItem(T lastItem) {
        this.lastItem = lastItem;
    }

    public boolean isHasMorePages() {
        return hasMorePages;
    }

    public void setHasMorePages(boolean hasMorePages) {
        this.hasMorePages = hasMorePages;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isGettingUser() {
        return isGettingUser;
    }

    public void setGettingUser(boolean gettingUser) {
        this.isGettingUser = gettingUser;
    }

    public void loadMoreItems() {
        if (!isLoading && hasMorePages) {
            setLoading(true);
            view.setLoading(true);

            getItems(authToken, user, PAGE_SIZE, lastItem);
        }
    }

    public void getUser(String clickable) {
        if (clickable.contains("http")) {
            view.navigateToURL(clickable);
        } else {
            new UserService().getUser(clickable, new GetUserObserver(view));
            view.displayErrorMessage("","Getting user's profile...");
        }
    }

    public abstract void getItems(AuthToken authToken, User user, int pageSize, T lastItem);

}
