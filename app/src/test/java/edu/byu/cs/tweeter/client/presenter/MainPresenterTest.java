package edu.byu.cs.tweeter.client.presenter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.text.ParseException;

import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.model.service.observer.PostStatusObserver;
import edu.byu.cs.tweeter.model.domain.User;

public class MainPresenterTest {

    private MainPresenter.MainView mockMainView;
    private StatusService mockStatusService;
    private PostStatusObserver postStatusObserverSpy;
    private String post;
    private User user;

    private MainPresenter mainPresenterSpy;

    @BeforeEach
    public void setup() {
        mockMainView = Mockito.mock(MainPresenter.MainView.class);
        mockStatusService = Mockito.mock(StatusService.class);

        postStatusObserverSpy = Mockito.spy(new PostStatusObserver(mockMainView));
        mainPresenterSpy = Mockito.spy(new MainPresenter(mockMainView));

        Mockito.when(mainPresenterSpy.getStatusService()).thenReturn(mockStatusService);

        post = "Random post";
        user = new User(null,null,null);
    }

    @Test
    public void testPostStatus_postStatusSucceeded() throws ParseException {
        postTestStatus(getAnswer("success"));

        Mockito.verify(mockMainView).displayInfoMessage("posting", "Posting Status...");
        Mockito.verify(mockMainView).displayInfoMessage("posting", "Successfully posted!");
        Mockito.verify(mockMainView).clearErrorMessage("posting");
    }

    @Test
    public void testPostStatus_postStatusFailed() throws ParseException {
        postTestStatus(getAnswer("failure"));

        verifyResult("Failed to post status: ");
    }

    @Test
    public void testPostStatus_postStatusFailedWithException() throws ParseException {
        postTestStatus(getAnswer("exception"));

        verifyResult("Failed to post status because of exception: ");
    }

    private Answer<Void> getAnswer(String type) {
        return new Answer<>() {
            @Override
            public Void answer(InvocationOnMock invocation) throws Throwable {
                PostStatusObserver observer = postStatusObserverSpy;
                if(type.equals("success")) {
                    observer.handleSuccess();
                } else if (type.equals("failure")) {
                    observer.handleFailure("something bad happened");
                } else {
                    observer.handleException(new Exception("something bad happened"));
                }
                return null;
            }
        };
    }

    private void postTestStatus(Answer<Void> answer) throws ParseException {
        //Mockito.doAnswer(answer).when(mockStatusService).postStatus(Mockito.any(), Mockito.any());
        //mainPresenterSpy.initiatePostStatus(post, user);
    }

    private void verifyResult(String s) {
        Mockito.verify(mockMainView).displayInfoMessage("posting", "Posting Status...");
        Mockito.verify(mockMainView).clearInfoMessage("posting");
        Mockito.verify(mockMainView).displayErrorMessage("posting", s + "something bad happened");
    }

}
