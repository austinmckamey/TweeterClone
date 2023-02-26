package edu.byu.cs.tweeter.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import edu.byu.cs.tweeter.client.model.net.ServerFacade;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.PostStatusObserver;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.AuthToken;
import edu.byu.cs.tweeter.model.domain.Status;
import edu.byu.cs.tweeter.model.domain.User;
import edu.byu.cs.tweeter.model.net.TweeterRemoteException;
import edu.byu.cs.tweeter.model.net.request.LoginRequest;
import edu.byu.cs.tweeter.model.net.request.StoryRequest;
import edu.byu.cs.tweeter.model.net.response.LoginResponse;
import edu.byu.cs.tweeter.model.net.response.StoryResponse;

public class M4CTest {

    private ServerFacade facade;

    private CountDownLatch countDownLatch;

    private MainPresenter.MainView mockMainView;
    private PostStatusObserver postStatusObserverSpy;
    private String post;
    private User user;
    private AuthToken authToken;
    private Status status;

    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup() {
        facade = new ServerFacade();

        mockMainView = Mockito.mock(MainPresenter.MainView.class);

        postStatusObserverSpy = Mockito.spy(new PostStatusObserver(mockMainView));
        mainPresenterSpy = Mockito.spy(new MainPresenter(mockMainView));

        post = "Random post";

        resetCountDownLatch();
    }

    private void resetCountDownLatch() {
        countDownLatch = new CountDownLatch(1);
    }

    private void awaitCountDownLatch() throws InterruptedException {
        countDownLatch.await();
        resetCountDownLatch();
    }

    @Test
    public void testLogin() throws IOException, TweeterRemoteException, InterruptedException, ParseException {
        String username = "@me";
        String password = "password";

        LoginRequest loginRequest = new LoginRequest(username, password);
        LoginResponse loginResponse = facade.login(loginRequest, "/login");

        user = loginResponse.getUser();
        authToken = loginResponse.getAuthToken();

        assertNotNull(user);
        assertNotNull(authToken);

        status = new Status(post, user, getFormattedDateTime(), parseURLs(post), parseMentions(post));
        Mockito.doAnswer(getAnswer()).when(mockMainView).displayInfoMessage(Mockito.any(), Mockito.any());
        mainPresenterSpy.initiatePostStatus(authToken, post, user);
        awaitCountDownLatch();

        Mockito.verify(mockMainView).displayInfoMessage("posting", "Successfully posted!");

        StoryRequest storyRequest = new StoryRequest(authToken, user.getAlias(), 10, null);
        StoryResponse storyResponse = facade.getStory(storyRequest, "/getstory");
        List<Status> story = storyResponse.getStory();

        assertEquals(status, story.get(0));
    }

    private Answer<Void> getAnswer() {
        return new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                countDownLatch.countDown();
                return null;
            }
        };
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
