package edu.byu.cs.tweeter.client.presenter;

import com.google.rpc.context.AttributeContext;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import edu.byu.cs.tweeter.client.model.service.FollowService;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.UserService;
import edu.byu.cs.tweeter.client.model.service.observer.CountObserver;
import edu.byu.cs.tweeter.client.model.service.observer.FollowObserver;
import edu.byu.cs.tweeter.client.model.service.observer.IsFollowerObserver;
import edu.byu.cs.tweeter.client.model.service.observer.LogoutObserver;
import edu.byu.cs.tweeter.client.model.service.observer.PostStatusObserver;
import edu.byu.cs.tweeter.client.view.MessageView;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenter extends Presenter {

    public interface MainView extends MessageView {
        void logoutUser();
        void updateIsFollower(boolean isFollower);
        void updateFollowersCount(int count);
        void updateFollowingCount(int count);
        void updateFollowAndUnfollow(boolean added);
    }

    private MainView view;
    private StatusService service;

    public MainPresenter(MainView view) {
        this.view = view;
    }

    public StatusService getStatusService() {
        if (service == null) {
            return new StatusService();
        }
        return service;
    }

    public void initiateLogout(AuthToken authToken) {
        view.displayInfoMessage("logout","Logging Out...");
        new UserService().logout(authToken,new LogoutObserver(view));
    }

    public void initiatePostStatus(AuthToken authToken, String post, User user) throws ParseException {
        //view.displayInfoMessage("posting", "Posting Status...");
        Status newStatus = new Status(post, user, getFormattedDateTime(), parseURLs(post), parseMentions(post));
        getStatusService().postStatus(authToken, newStatus,new PostStatusObserver(view));
    }

    public void initiateIsFollower(User user) {
        new FollowService().isFollower(user, new IsFollowerObserver(view));
    }

    public void initiateGetFollowersAndFollowingCount(User user) {
        new FollowService().getFollowersAndFollowingCount(user, new CountObserver(view));
    }

    public void initiateFollow(User user) {
        view.displayInfoMessage("other", "Adding " + user.getName() + "...");
        new FollowService().follow(user, new FollowObserver(view,true));
    }

    public void initiateUnfollow(User user) {
        view.displayInfoMessage("other", "Removing " + user.getName() + "...");
        new FollowService().unfollow(user, new FollowObserver(view,false));
    }

    public String getFormattedDateTime() throws ParseException {
        SimpleDateFormat userFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat statusFormat = new SimpleDateFormat("MMM d yyyy h:mm aaa");

        return statusFormat.format(userFormat.parse(LocalDate.now().toString() + " " + LocalTime.now().toString().substring(0, 8)));
    }

    public List<String> parseURLs(String post) {
        List<String> containedUrls = new ArrayList<>();
        for (String word : post.split("\\s")) {
            if (word.startsWith("http://") || word.startsWith("https://")) {

                int index = findUrlEndIndex(word);

                word = word.substring(0, index);

                containedUrls.add(word);
            }
        }

        return containedUrls;
    }

    public List<String> parseMentions(String post) {
        List<String> containedMentions = new ArrayList<>();

        for (String word : post.split("\\s")) {
            if (word.startsWith("@")) {
                word = word.replaceAll("[^a-zA-Z0-9]", "");
                word = "@".concat(word);

                containedMentions.add(word);
            }
        }

        return containedMentions;
    }

    public int findUrlEndIndex(String word) {
        if (word.contains(".com")) {
            int index = word.indexOf(".com");
            index += 4;
            return index;
        } else if (word.contains(".org")) {
            int index = word.indexOf(".org");
            index += 4;
            return index;
        } else if (word.contains(".edu")) {
            int index = word.indexOf(".edu");
            index += 4;
            return index;
        } else if (word.contains(".net")) {
            int index = word.indexOf(".net");
            index += 4;
            return index;
        } else if (word.contains(".mil")) {
            int index = word.indexOf(".mil");
            index += 4;
            return index;
        } else {
            return word.length();
        }
    }
}
